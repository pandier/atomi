package io.github.pandier.atomi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

public interface AtomiEntityData {
    @NotNull
    Tristate permission(@NotNull String permission);

    void setPermission(@NotNull String permission, @NotNull Tristate value);

    @NotNull
    Map<String, Boolean> permissions();

    @NotNull
    <T> Optional<T> option(@NotNull AtomiOption<T> option);

    <T> void setOption(@NotNull AtomiOption<T> option, @Nullable T value);

    @NotNull
    Map<AtomiOption<?>, Object> options();
}
