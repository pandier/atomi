package io.github.pandier.atomi.internal;

import io.github.pandier.atomi.AtomiEntity;
import io.github.pandier.atomi.AtomiOption;
import io.github.pandier.atomi.Tristate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@ApiStatus.Internal
public abstract class AbstractAtomiEntity implements AtomiEntity {
    protected final AbstractAtomi atomi;

    protected AbstractAtomiEntity(AbstractAtomi atomi) {
        this.atomi = atomi;
    }

    @Override
    public @NotNull Tristate permission(@NotNull String permission) {
        Tristate value = data().permission(permission);
        if (value != Tristate.UNSET)
            return value;
        for (AtomiEntity parent : parents()) {
            Tristate parentValue = parent.permission(permission);
            if (parentValue != Tristate.UNSET)
                return parentValue;
        }
        return defaultPermission(permission);
    }

    protected @NotNull Tristate defaultPermission(@NotNull String permission) {
        return Tristate.UNSET;
    }

    @Override
    public @NotNull <T> Optional<T> option(@NotNull AtomiOption<T> option) {
        Optional<T> value = data().option(option);
        if (value.isPresent())
            return value;
        for (AtomiEntity parent : parents()) {
            value = parent.option(option);
            if (value.isPresent())
                return value;
        }
        return Optional.empty();
    }
}
