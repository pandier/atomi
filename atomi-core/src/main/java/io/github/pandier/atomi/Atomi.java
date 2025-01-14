package io.github.pandier.atomi;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

public interface Atomi {
    @NotNull
    AtomiUser user(@NotNull UUID uuid);

    @NotNull
    Optional<AtomiUser> userOptional(@NotNull UUID uuid);

    boolean userExists(@NotNull UUID uuid);

    @NotNull
    Optional<AtomiUser> userFromCache(@NotNull UUID uuid);

    @NotNull
    Collection<AtomiUser> cachedUsers();

    void unloadUser(@NotNull UUID uuid);

    @NotNull
    Optional<AtomiGroup> group(@NotNull String name);

    boolean groupExists(@NotNull String name);

    @NotNull
    AtomiGroup getOrCreateGroup(@NotNull String name);

    boolean removeGroup(@NotNull String name);

    void removeGroup(@NotNull AtomiGroup group);

    @NotNull
    AtomiGroup defaultGroup();

    @NotNull
    Set<String> groupNames();

    @NotNull
    Collection<AtomiGroup> groups();

    @NotNull
    Predicate<String> groupNameValidityPredicate();

    @NotNull
    Predicate<String> permissionValidityPredicate();
}
