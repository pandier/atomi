package io.github.pandier.atomi;

import io.github.pandier.atomi.internal.AtomiContextImpl;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface AtomiContext extends AtomiEntity {

    @NotNull
    static AtomiContext.Builder builder(@NotNull Key key) {
        return new AtomiContextImpl.Builder(key);
    }

    @NotNull
    Key key();

    interface Builder {
        Builder setPermission(@NotNull String permission, @NotNull Tristate value);

        <T> Builder setOption(@NotNull AtomiOption<T> option, @Nullable T value);

        AtomiContext build();
    }
}
