package io.github.pandier.atomi.internal;

import io.github.pandier.atomi.AtomiEntity;
import io.github.pandier.atomi.Tristate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Function;

@ApiStatus.Internal
public abstract class AbstractAtomiEntity implements AtomiEntity {
    protected final AbstractAtomi atomi;

    protected AbstractAtomiEntity(AbstractAtomi atomi) {
        this.atomi = atomi;
    }

    protected <T> @NotNull Optional<T> findInParents(Function<AtomiEntity, Optional<T>> function) {
        Optional<T> value = function.apply(this);
        if (value.isPresent())
            return value;
        for (AtomiEntity parent : parents()) {
            value = function.apply(parent);
            if (value.isPresent())
                return value;
        }
        return Optional.empty();
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
    public @NotNull Optional<Component> prefix() {
        return findInParents(AtomiEntity::prefix);
    }

    @Override
    public @NotNull Optional<Component> suffix() {
        return findInParents(AtomiEntity::suffix);
    }

    @Override
    public @NotNull Optional<NamedTextColor> color() {
        return findInParents(AtomiEntity::color);
    }
}
