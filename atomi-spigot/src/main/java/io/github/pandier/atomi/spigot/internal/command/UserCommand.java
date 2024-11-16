package io.github.pandier.atomi.spigot.internal.command;

import dev.jorel.commandapi.arguments.*;
import io.github.pandier.atomi.AtomiMetadata;
import io.github.pandier.atomi.AtomiUser;
import io.github.pandier.atomi.Tristate;
import io.github.pandier.atomi.spigot.SpigotAtomi;
import io.github.pandier.atomi.spigot.internal.command.info.InfoBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;

import static io.github.pandier.atomi.spigot.internal.command.Commands.createGroupArgument;

@ApiStatus.Internal
public class UserCommand {
    public static Argument<?> create() {
        return new LiteralArgument("user")
                .then(new OfflinePlayerArgument("player")
                        .then(new LiteralArgument("permission")
                                .then(new TextArgument("permission")
                                        .then(new BooleanArgument("value")
                                                .executes((sender, args) -> {
                                                    return setPermission(sender, (OfflinePlayer) args.get("player"), (String) args.get("permission"), Tristate.of((Boolean) args.get("value")));
                                                }))
                                        .then(new LiteralArgument("unset")
                                                .executes((sender, args) -> {
                                                    return setPermission(sender, (OfflinePlayer) args.get("player"), (String) args.get("permission"), Tristate.UNSET);
                                                }))))
                        .then(new LiteralArgument("metadata")
                                .then(createMetadataArgument("prefix", AdventureChatComponentArgument::new, x -> x, AtomiMetadata::setPrefix))
                                .then(createMetadataArgument("suffix", AdventureChatComponentArgument::new, x -> x, AtomiMetadata::setSuffix))
                                .then(createMetadataArgument("color", AdventureChatColorArgument::new, x -> Component.text(x.toString()).color(x), AtomiMetadata::setColor)))
                        .then(new LiteralArgument("group")
                                .then(createGroupArgument()
                                        .executes((sender, args) -> {
                                            return group(sender, (OfflinePlayer) args.get("player"), (String) args.get("group"));
                                        })))
                        .then(new LiteralArgument("info")
                                .executes((sender, args) -> {
                                    info(sender, (OfflinePlayer) args.get("player"));
                                })));
    }

    @SuppressWarnings("unchecked")
    private static <T> Argument<?> createMetadataArgument(String name, Function<String, Argument<T>> valueArgumentFactory, Function<T, Component> display, BiConsumer<AtomiMetadata, T> setter) {
        return new LiteralArgument(name)
                .then(new LiteralArgument("set")
                        .then(valueArgumentFactory.apply("value")
                                .executes((sender, args) -> {
                                    return setMetadata(sender, (OfflinePlayer) args.get("player"), name, (T) args.get("value"), display, setter);
                                })))
                .then(new LiteralArgument("unset")
                        .executes((sender, args) -> {
                            return setMetadata(sender, (OfflinePlayer) args.get("player"), name, null, display, setter);
                        }));
    }

    private static int group(CommandSender sender, OfflinePlayer player, String group) {
        AtomiUser user = SpigotAtomi.get().user(player);
        if (!user.setGroupByName(group)) {
            Commands.send(sender, Component.text("Couldn't find group with the name '" + group + "'").color(NamedTextColor.RED), true);
            return 0;
        }

        Commands.send(sender, Component.text("Group of ")
                .append(Component.text(String.valueOf(player.getName())).color(NamedTextColor.WHITE))
                .append(Component.text(" set to "))
                .append(Component.text(group).color(NamedTextColor.GOLD)), true);
        return 1;
    }

    private static int unsetPermission(CommandSender sender, OfflinePlayer player, String permission) {
        return setPermission(sender, player, permission, Tristate.UNSET);
    }

    private static int setPermission(CommandSender sender, OfflinePlayer player, String permission, Tristate value) {
        if (!SpigotAtomi.get().permissionValidityPredicate().test(permission)) {
            Commands.send(sender, Component.text("Permission '" + permission + "' contains illegal characters").color(NamedTextColor.RED), true);
            return 0;
        }

        AtomiUser user = SpigotAtomi.get().user(player);

        user.setPermission(permission, value);
        Component response = Component.text("Permission ")
                .append(Component.text(permission).color(NamedTextColor.AQUA))
                .append(Component.text(" for user "))
                .append(Component.text(String.valueOf(player.getName())).color(NamedTextColor.WHITE));
        if (value == Tristate.UNSET) {
            response = response.append(Component.text(" unset"));
        } else {
            response = response.append(Component.text(" set to ")
                    .append(Component.text(value.name().toLowerCase()).color(value == Tristate.TRUE ? NamedTextColor.GREEN : NamedTextColor.RED)));
        }
        Commands.send(sender, response, true);
        return 1;
    }

    private static <T> int setMetadata(CommandSender sender, OfflinePlayer player, String key, @Nullable T value, Function<T, Component> display, BiConsumer<AtomiMetadata, T> setter) {
        AtomiUser user = SpigotAtomi.get().user(player);

        setter.accept(user.metadata(), value);

        String capitalizedKey = Character.toUpperCase(key.charAt(0)) + key.substring(1) + " for user ";
        if (value != null) {
            Commands.send(sender, Component.text(capitalizedKey)
                    .append(Component.text(String.valueOf(player.getName())).color(NamedTextColor.WHITE))
                    .append(Component.text(" set to "))
                    .append(Component.empty().color(NamedTextColor.WHITE).append(display.apply(value))), true);
        } else {
            Commands.send(sender, Component.text(capitalizedKey)
                    .append(Component.text(String.valueOf(player.getName())).color(NamedTextColor.WHITE))
                    .append(Component.text(" unset")), true);
        }

        return 1;
    }

    private static int info(CommandSender sender, OfflinePlayer player) {
        AtomiUser user = SpigotAtomi.get().user(player);
        Commands.send(sender, InfoBuilder.player(player.getName(), user), false);
        return 1;
    }
}
