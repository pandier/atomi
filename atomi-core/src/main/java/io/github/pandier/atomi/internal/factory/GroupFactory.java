package io.github.pandier.atomi.internal.factory;

import io.github.pandier.atomi.AtomiGroup;
import io.github.pandier.atomi.internal.AtomiEntityDataImpl;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
@ApiStatus.Internal
public interface GroupFactory {
    @NotNull AtomiGroup create(@NotNull String name, @NotNull AtomiEntityDataImpl data);
}
