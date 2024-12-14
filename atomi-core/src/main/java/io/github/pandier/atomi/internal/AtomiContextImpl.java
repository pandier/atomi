package io.github.pandier.atomi.internal;

import io.github.pandier.atomi.AtomiContext;
import io.github.pandier.atomi.AtomiEntityData;
import io.github.pandier.atomi.AtomiOption;
import io.github.pandier.atomi.Tristate;
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
        private final HashMap<AtomiOption<?>, Object> options = new HashMap<>();

        public Builder(@NotNull String identifier) {
            this.identifier = identifier;
        }

        public Builder setIdentifier(@NotNull String identifier) {
            this.identifier = identifier;
            return this;
        }

        public Builder setPermission(@NotNull String permission, @NotNull Tristate value) {
            permissions.put(permission, value.asNullableBoolean());
            return this;
        }

        @Override
        public <T> Builder setOption(@NotNull AtomiOption<T> option, @Nullable T value) {
            if (value == null) {
                options.remove(option);
            } else {
                options.put(option, value);
            }
            return this;
        }

        @Override
        public AtomiContext build() {
            return new AtomiContextImpl(identifier, new AtomiEntityDataImpl(permissions, options));
        }
    }
}
