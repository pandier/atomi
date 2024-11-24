package io.github.pandier.atomi.internal;

import io.github.pandier.atomi.AtomiContext;
import io.github.pandier.atomi.AtomiEntityData;
import io.github.pandier.atomi.Tristate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

@ApiStatus.Internal
public class AtomiContextImpl implements AtomiContext, NoParentAtomiEntity {
    private final String identifier;
    private final AtomiEntityDataImpl data;

    private AtomiContextImpl(String identifier, AtomiEntityDataImpl data) {
        this.identifier = identifier;
        this.data = data;
    }

    @Override
    public @NotNull String identifier() {
        return identifier;
    }

    @Override
    public @NotNull AtomiEntityData data() {
        return data;
    }


    @ApiStatus.Internal
    public static class Builder implements AtomiContext.Builder {
        private String identifier;
        private final HashMap<String, Boolean> permissions = new HashMap<>();
        private Component prefix;
        private Component suffix;
        private NamedTextColor color;

        public Builder(@NotNull String identifier) {
            this.identifier = identifier;
        }

        public Builder setIdentifier(@NotNull String identifier) {
            this.identifier = identifier;
            return this;
        }

        public Builder setPermission(@NotNull String permission, Tristate value) {
            permissions.put(permission, value.asNullableBoolean());
            return this;
        }

        @Override
        public AtomiContext.Builder setPrefix(@Nullable Component prefix) {
            this.prefix = prefix;
            return this;
        }

        @Override
        public AtomiContext.Builder setSuffix(@Nullable Component suffix) {
            this.suffix = suffix;
            return this;
        }

        @Override
        public AtomiContext.Builder setColor(@Nullable NamedTextColor color) {
            this.color = color;
            return this;
        }

        @Override
        public AtomiContext build() {
            return new AtomiContextImpl(identifier, new AtomiEntityDataImpl(permissions, prefix, suffix, color));
        }
    }
}
