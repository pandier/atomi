package io.github.pandier.atomi.internal.storage;

import io.github.pandier.atomi.AtomiGroup;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@ApiStatus.Internal
public interface GroupStorage {

    @NotNull Map<String, AtomiGroup> load() throws StorageException;

    void save(Map<String, AtomiGroup> groups) throws StorageException;
}
