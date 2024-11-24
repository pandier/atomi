package io.github.pandier.atomi;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public interface AtomiEntity {
    @NotNull
    AtomiEntityData data();

    default boolean hasPermission(@NotNull String permission) {
        return permission(permission) == Tristate.TRUE;
    }

    @NotNull
    Tristate permission(@NotNull String permission);

    default void setPermission(@NotNull String permission, @NotNull Tristate value) {
        data().setPermission(permission, value);
    }

    @NotNull
    Optional<Component> prefix();

    default void setPrefix(@Nullable Component prefix) {
        data().setPrefix(prefix);
    }

    @NotNull
    Optional<Component> suffix();

    default void setSuffix(@Nullable Component suffix) {
        data().setSuffix(suffix);
    }

    @NotNull
    Optional<NamedTextColor> color();

    default void setColor(@Nullable NamedTextColor color) {
        data().setColor(color);
    }

    @NotNull
    List<AtomiEntity> parents();
}
