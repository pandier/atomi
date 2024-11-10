package io.github.pandier.atomi;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface Atomi {
    @NotNull
    Optional<AtomiUser> user(@NotNull UUID uuid);

    @NotNull
    AtomiUser getOrCreateUser(@NotNull UUID uuid);

    @NotNull
    Optional<AtomiGroup> group(@NotNull String name);

    @NotNull
    AtomiGroup getOrCreateGroup(@NotNull String name);

    boolean removeGroup(@NotNull String name);

    void removeGroup(@NotNull AtomiGroup group);

    @NotNull
    Set<String> groupNames();

    @NotNull
    Collection<AtomiGroup> groups();

    @NotNull
    AtomiGroup defaultGroup();

    boolean isValidGroupName(@NotNull String name);

    boolean isValidPermission(@NotNull String permission);
}
