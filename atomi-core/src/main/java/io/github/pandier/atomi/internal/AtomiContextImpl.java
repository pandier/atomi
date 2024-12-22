package io.github.pandier.atomi.internal;

import io.github.pandier.atomi.AtomiContext;
import io.github.pandier.atomi.AtomiEntityData;
import io.github.pandier.atomi.AtomiOption;
import io.github.pandier.atomi.Tristate;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;

@ApiStatus.Internal
public class AtomiContextImpl implements AtomiContext, NoParentAtomiEntity {
    private final Key key;
    private final AtomiEntityDataImpl data;

    private AtomiContextImpl(Key key, AtomiEntityDataImpl data) {
        this.key = key;
        this.data = data;
    }

    @Override
    public @NotNull Key key() {
        return key;
    }

    @Override
    public @NotNull AtomiEntityData data() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AtomiContextImpl that = (AtomiContextImpl) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(key);
    }

    @Override
    public String toString() {
        return "AtomiContextImpl{" +
                "key=" + key +
                '}';
    }

    @ApiStatus.Internal
    public static class Builder implements AtomiContext.Builder {
        private final Key key;
        private final HashMap<String, Boolean> permissions = new HashMap<>();
        private final HashMap<AtomiOption<?>, Object> options = new HashMap<>();

        public Builder(@NotNull Key key) {
            this.key = key;
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
            return new AtomiContextImpl(key, new AtomiEntityDataImpl(permissions, options));
        }
    }
}
