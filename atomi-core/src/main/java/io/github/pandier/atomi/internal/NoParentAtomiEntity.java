package io.github.pandier.atomi.internal;

import io.github.pandier.atomi.AtomiEntity;
import io.github.pandier.atomi.Tristate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

@ApiStatus.Internal
public interface NoParentAtomiEntity extends AtomiEntity {

    @Override
    @NotNull
    default Tristate permission(@NotNull String permission) {
        return data().permission(permission);
    }

    @Override
    @NotNull
    default Optional<Component> prefix() {
        return data().prefix();
    }

    @Override
    default @NotNull Optional<Component> suffix() {
        return data().suffix();
    }

    @Override
    @NotNull
    default Optional<NamedTextColor> color() {
        return data().color();
    }

    @Override
    @NotNull
    default List<AtomiEntity> parents() {
        return List.of();
    }
}
