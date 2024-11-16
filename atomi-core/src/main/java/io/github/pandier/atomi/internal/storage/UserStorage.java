package io.github.pandier.atomi.internal.storage;

import io.github.pandier.atomi.AtomiUser;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public interface UserStorage {

    @NotNull Optional<AtomiUser> load(@NotNull UUID uuid) throws StorageException;

    void save(@NotNull AtomiUser user) throws StorageException;

    boolean exists(@NotNull UUID uuid);

    void delete(@NotNull UUID uuid) throws StorageException;

    default void delete(@NotNull AtomiUser user) throws StorageException {
        delete(user.uuid());
    }
}
