package io.github.pandier.atomi.spigot.internal.command;

import dev.jorel.commandapi.arguments.*;
import io.github.pandier.atomi.AtomiGroup;
import io.github.pandier.atomi.internal.option.AtomiOptionRegistry;
import io.github.pandier.atomi.AtomiOption;
import io.github.pandier.atomi.Tristate;
import io.github.pandier.atomi.spigot.SpigotAtomi;
import io.github.pandier.atomi.spigot.internal.command.info.InfoBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static io.github.pandier.atomi.spigot.internal.command.Commands.createGroupArgument;

@ApiStatus.Internal
public class GroupCommand {
    public static Argument<?> create(AtomiOptionRegistry optionRegistry) {
        return new LiteralArgument("group")
                .then(new LiteralArgument("create")
                        .then(new StringArgument("name")
                                .executes((sender, args) -> {
                                    return create(sender, (String) args.get("name"));
                                })))
                .then(new LiteralArgument("remove")
                        .then(createGroupArgument()
                                .executes(((sender, args) -> {
                                    return remove(sender, (String) args.get("group"));
                                }))))
                .then(new LiteralArgument("permission")
                        .then(createGroupArgument()
                                .then(new TextArgument("permission")
                                        .then(new BooleanArgument("value")
                                                .executes((sender, args) -> {
                                                    return setPermission(sender, (String) args.get("group"), (String) args.get("permission"), Tristate.of((Boolean) args.get("value")));
                                                }))
                                        .then(new LiteralArgument("unset")
                                                .executes((sender, args) -> {
                                                    return unsetPermission(sender, (String) args.get("group"), (String) args.get("permission"));
                                                })))))
                .then(new LiteralArgument("option")
                        .then(appendOptionArguments(createGroupArgument(), optionRegistry)))
                .then(new LiteralArgument("info")
                        .then(createGroupArgument()
                                .executes((sender, args) -> {
                                    return info(sender, (String) args.get("group"));
                                })));
    }

    private static Argument<?> appendOptionArguments(Argument<?> argument, AtomiOptionRegistry optionRegistry) {
        optionRegistry.forEach(option -> appendOptionArgument(argument, option));
        return argument;
    }

    @SuppressWarnings("unchecked")
    private static <T> void appendOptionArgument(Argument<?> argument, AtomiOption<T> option) {
        argument.then(new LiteralArgument(option.name())
                .then(new LiteralArgument("set")
                        .then(AtomiOptionTypeArguments.createArgument(option.type(), "value")
                                .executes((sender, args) -> {
                                    return setOption(sender, (String) args.get("group"), option, (T) args.get("value"));
                                })))
                .then(new LiteralArgument("unset")
                        .executes((sender, args) -> {
                            return setOption(sender, (String) args.get("group"), option, null);
                        })));
    }

    private static int create(CommandSender sender, String name) {
        if (SpigotAtomi.get().group(name).isPresent()) {
            Commands.send(sender, Component.text("Group with the name '" + name + "' already exists").color(NamedTextColor.RED), true);
            return 0;
        }

        SpigotAtomi.get().getOrCreateGroup(name);
        Commands.send(sender, Component.text("Created group ")
                .append(Component.text(name).color(NamedTextColor.WHITE)), true);
        return 1;
    }

    private static int remove(CommandSender sender, String name) {
        AtomiGroup group = SpigotAtomi.get().group(name).orElse(null);
        if (group == null) {
            Commands.send(sender, Component.text("Couldn't find group with the name '" + name + "'").color(NamedTextColor.RED), true);
            return 0;
        }

        if (group.isDefault()) {
            Commands.send(sender, Component.text("Default group cannot be removed").color(NamedTextColor.RED), true);
            return 0;
        }

        SpigotAtomi.get().removeGroup(name);
        Commands.send(sender, Component.text("Removed group ")
                .append(Component.text(name).color(NamedTextColor.WHITE)), true);
        return 1;
    }

    private static int unsetPermission(CommandSender sender, String groupName, String permission) {
        return setPermission(sender, groupName, permission, Tristate.UNSET);
    }

    private static int setPermission(CommandSender sender, String groupName, String permission, Tristate value) {
        if (!SpigotAtomi.get().permissionValidityPredicate().test(permission)) {
            Commands.send(sender, Component.text("Permission '" + permission + "' contains illegal characters").color(NamedTextColor.RED), true);
            return 0;
        }

        AtomiGroup group = SpigotAtomi.get().group(groupName).orElse(null);
        if (group == null) {
            Commands.send(sender, Component.text("Group with the name '" + groupName + "' does not exist").color(NamedTextColor.RED), true);
            return 0;
        }

        group.setPermission(permission, value);
        Component response = Component.text("Permission ")
                .append(Component.text(permission).color(NamedTextColor.AQUA))
                .append(Component.text(" for group "))
                .append(Component.text(groupName).color(NamedTextColor.WHITE));
        if (value == Tristate.UNSET) {
            response = response.append(Component.text(" unset"));
        } else {
            response = response.append(Component.text(" set to ")
                    .append(Component.text(value.name().toLowerCase()).color(value == Tristate.TRUE ? NamedTextColor.GREEN : NamedTextColor.RED)));
        }
        Commands.send(sender, response, true);
        return 1;
    }

    private static <T> int setOption(CommandSender sender, String groupName, @NotNull AtomiOption<T> option, @Nullable T value) {
        AtomiGroup group = SpigotAtomi.get().group(groupName).orElse(null);
        if (group == null) {
            Commands.send(sender, Component.text("Group with the name '" + groupName + "' does not exist").color(NamedTextColor.RED), true);
            return 0;
        }

        group.setOption(option, value);

        Component text = Component.text("Option ")
                .append(Component.text(option.name()).color(NamedTextColor.LIGHT_PURPLE))
                .append(Component.text(" for user "))
                .append(Component.text(groupName).color(NamedTextColor.WHITE));
        if (value != null) {
            text = text.append(Component.text(" set to "))
                    .append(Component.empty().color(NamedTextColor.WHITE).append(option.type().displayText(value)));
        } else {
            text = text.append(Component.text(" unset"));
        }
        Commands.send(sender, text, true);

        return 1;
    }

    private static int info(CommandSender sender, String groupName) {
        AtomiGroup group = SpigotAtomi.get().group(groupName).orElse(null);
        if (group == null) {
            Commands.send(sender, Component.text("Group with the name '" + groupName + "' does not exist").color(NamedTextColor.RED), true);
            return 0;
        }

        Commands.send(sender, InfoBuilder.group(group), false);
        return 1;
    }
}
