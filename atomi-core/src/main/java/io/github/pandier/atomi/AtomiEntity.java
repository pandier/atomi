package io.github.pandier.atomi;

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
    <T> Optional<T> option(@NotNull AtomiOption<T> option);

    default <T> void setOption(@NotNull AtomiOption<T> option, @Nullable T value) {
        data().setOption(option, value);
    }

    @NotNull
    List<AtomiEntity> parents();
}
