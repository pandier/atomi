package io.github.pandier.atomi;

import io.github.pandier.atomi.internal.AtomiContextImpl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface AtomiContext extends AtomiEntity {

    @NotNull
    static AtomiContext.Builder builder(@NotNull String identifier) {
        return new AtomiContextImpl.Builder(identifier);
    }

    @NotNull
    String identifier();

    interface Builder {
        Builder setIdentifier(@NotNull String identifier);

        Builder setPermission(@NotNull String permission, Tristate value);

        Builder setPrefix(@Nullable Component prefix);

        Builder setSuffix(@Nullable Component suffix);

        Builder setColor(@Nullable NamedTextColor color);

        AtomiContext build();
    }
}
