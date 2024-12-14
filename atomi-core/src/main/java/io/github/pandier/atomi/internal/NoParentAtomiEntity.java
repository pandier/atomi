package io.github.pandier.atomi.internal;

import io.github.pandier.atomi.AtomiEntity;
import io.github.pandier.atomi.AtomiOption;
import io.github.pandier.atomi.Tristate;
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
    default <T> Optional<T> option(@NotNull AtomiOption<T> option) {
        return data().option(option);
    }

    @Override
    @NotNull
    default List<AtomiEntity> parents() {
        return List.of();
    }
}
