package io.github.pandier.atomi.internal.factory;

import io.github.pandier.atomi.AtomiUser;
import io.github.pandier.atomi.internal.AtomiUserDataImpl;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@FunctionalInterface
@ApiStatus.Internal
public interface UserFactory {
    @NotNull AtomiUser create(@NotNull UUID uuid, @NotNull AtomiUserDataImpl data);
}
