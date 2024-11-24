package io.github.pandier.atomi;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
    Optional<Component> prefix();

    void setPrefix(@Nullable Component prefix);

    @NotNull
    Optional<Component> suffix();

    void setSuffix(@Nullable Component suffix);

    @NotNull
    Optional<NamedTextColor> color();

    void setColor(@Nullable NamedTextColor color);
}
