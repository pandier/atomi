package io.github.pandier.atomi.internal.command;

import io.github.pandier.atomi.Tristate;
import io.github.pandier.atomi.internal.command.argument.LiteralAtomiArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public abstract class AbstractCommand {

    @NotNull
    public abstract LiteralAtomiArgument create();

    @NotNull
    public static Component formatGroup(@NotNull String group) {
        return Component.text(group).color(NamedTextColor.GOLD);
    }

    @NotNull
    public static Component formatPermission(@NotNull String permission) {
        return Component.text(permission).color(NamedTextColor.AQUA);
    }

    @NotNull
    public static Component formatMetadata(@NotNull String permission) {
        return Component.text(permission).color(NamedTextColor.LIGHT_PURPLE);
    }

    @NotNull
    public static Component formatTristate(@NotNull Tristate value) {
        if (value == Tristate.UNSET)
            return Component.text("unset").color(NamedTextColor.GRAY);
        return formatBoolean(value.asBoolean());
    }

    @NotNull
    public static Component formatBoolean(boolean value) {
        return Component.text(String.valueOf(value)).color(value ? NamedTextColor.GREEN : NamedTextColor.RED);
    }
}
