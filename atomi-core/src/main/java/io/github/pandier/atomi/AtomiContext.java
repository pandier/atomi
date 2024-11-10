package io.github.pandier.atomi;

import io.github.pandier.atomi.internal.AtomiContextImpl;
import org.jetbrains.annotations.NotNull;

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

        Builder setMetadata(@NotNull AtomiMetadata metadata);

        AtomiContext build();
    }
}
