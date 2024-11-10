package io.github.pandier.atomi.spigot.internal.command;

import dev.jorel.commandapi.arguments.*;
import io.github.pandier.atomi.AtomiGroup;
import io.github.pandier.atomi.AtomiMetadata;
import io.github.pandier.atomi.Tristate;
import io.github.pandier.atomi.spigot.SpigotAtomi;
import io.github.pandier.atomi.spigot.internal.command.info.InfoBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;

import static io.github.pandier.atomi.spigot.internal.command.Commands.createGroupArgument;

@ApiStatus.Internal
public class GroupCommand {
    public static Argument<?> create() {
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
                .then(new LiteralArgument("metadata")
                        .then(createGroupArgument()
                                .then(createMetadataArgument("prefix", AdventureChatComponentArgument::new, x -> x, AtomiMetadata::setPrefix))
                                .then(createMetadataArgument("suffix", AdventureChatComponentArgument::new, x -> x, AtomiMetadata::setSuffix))
                                .then(createMetadataArgument("color", AdventureChatColorArgument::new, x -> Component.text(x.toString()).color(x), AtomiMetadata::setColor))))
                .then(new LiteralArgument("info")
                        .then(createGroupArgument()
                                .executes((sender, args) -> {
                                    return info(sender, (String) args.get("group"));
                                })));
    }

    @SuppressWarnings("unchecked")
    private static <T> Argument<?> createMetadataArgument(String name, Function<String, Argument<T>> valueArgumentFactory, Function<T, Component> display, BiConsumer<AtomiMetadata, T> setter) {
        return new LiteralArgument(name)
                .then(new LiteralArgument("set")
                        .then(valueArgumentFactory.apply("value")
                                .executes((sender, args) -> {
                                    return setMetadata(sender, (String) args.get("group"), name, (T) args.get("value"), display, setter);
                                })))
                .then(new LiteralArgument("unset")
                        .executes((sender, args) -> {
                            return setMetadata(sender, (String) args.get("group"), name, null, display, setter);
                        }));
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
        if (!SpigotAtomi.get().isValidPermission(permission)) {
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

    private static <T> int setMetadata(CommandSender sender, String groupName, String key, @Nullable T value, Function<T, Component> display, BiConsumer<AtomiMetadata, T> setter) {
        AtomiGroup group = SpigotAtomi.get().group(groupName).orElse(null);
        if (group == null) {
            Commands.send(sender, Component.text("Group with the name '" + groupName + "' does not exist").color(NamedTextColor.RED), true);
            return 0;
        }

        setter.accept(group.metadata(), value);

        String intro = Character.toUpperCase(key.charAt(0)) + key.substring(1) + " for group ";
        if (value != null) {
            Commands.send(sender, Component.text(intro)
                    .append(Component.text(groupName).color(NamedTextColor.WHITE))
                    .append(Component.text(" set to "))
                    .append(Component.empty().color(NamedTextColor.WHITE).append(display.apply(value))), true);
        } else {
            Commands.send(sender, Component.text(intro)
                    .append(Component.text(groupName).color(NamedTextColor.WHITE))
                    .append(Component.text(" unset")), true);
        }

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
