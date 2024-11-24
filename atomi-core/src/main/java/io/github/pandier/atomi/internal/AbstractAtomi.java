package io.github.pandier.atomi.internal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.pandier.atomi.Atomi;
import io.github.pandier.atomi.AtomiGroup;
import io.github.pandier.atomi.AtomiUser;
import io.github.pandier.atomi.internal.factory.GroupFactory;
import io.github.pandier.atomi.internal.factory.UserFactory;
import io.github.pandier.atomi.internal.storage.GroupStorage;
import io.github.pandier.atomi.internal.storage.StorageException;
import io.github.pandier.atomi.internal.storage.UserStorage;
import io.github.pandier.atomi.internal.storage.json.MultiJsonUserStorage;
import io.github.pandier.atomi.internal.storage.json.SingleJsonGroupStorage;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@ApiStatus.Internal
public abstract class AbstractAtomi implements Atomi {
    protected static final Pattern GROUP_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_\\-.+]+$");
    protected static final Pattern PERMISSION_PATTERN = Pattern.compile("^[a-zA-Z0-9_\\-.+*]+$");
    protected static final Predicate<String> GROUP_NAME_VALIDITY_PREDICATE = x -> GROUP_NAME_PATTERN.matcher(x).matches();
    protected static final Predicate<String> PERMISSION_VALIDITY_PREDICATE = x -> PERMISSION_PATTERN.matcher(x).matches();

    protected final String DEFAULT_GROUP_NAME = "default";

    protected final BiConsumer<String, Throwable> errorLogger;
    protected final UserFactory userFactory;
    protected final GroupFactory groupFactory;
    protected final DefaultPermissionProvider defaultPermissionProvider;

    protected final UserStorage userStorage;
    protected final GroupStorage groupStorage;

    protected final Map<String, AtomiGroup> groups;
    protected final Map<UUID, AtomiUser> userCache = new HashMap<>();

    protected AbstractAtomi(Path path, BiConsumer<String, Throwable> errorLogger) {
        this.errorLogger = errorLogger;
        this.userFactory = createUserFactory();
        this.groupFactory = createGroupFactory();
        this.defaultPermissionProvider = createDefaultPermissionProvider();

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        this.userStorage = new MultiJsonUserStorage(path.resolve("users"), this, userFactory, gson);
        this.groupStorage = new SingleJsonGroupStorage(path.resolve("groups.json"), groupFactory, gson);

        try {
            this.groups = groupStorage.load();
        } catch (StorageException e) {
            throw new IllegalStateException("Failed loading groups", e);
        }
    }

    @NotNull
    protected DefaultPermissionProvider createDefaultPermissionProvider() {
        return DefaultPermissionProvider.UNSET;
    }

    @NotNull
    protected UserFactory createUserFactory() {
        return (uuid, data) -> new AtomiUserImpl(this, uuid, data);
    }

    @NotNull
    protected GroupFactory createGroupFactory() {
        return (name, data) -> new AtomiGroupImpl(this, name, name.equals(DEFAULT_GROUP_NAME), data);
    }

    @Override
    public @NotNull AtomiUser user(@NotNull UUID uuid) {
        return userOptional(uuid).orElseGet(() -> {
            AtomiUser user = userFactory.create(uuid, new AtomiUserDataImpl(this));
            userCache.put(uuid, user);
            return user;
        });
    }

    @Override
    public @NotNull Optional<AtomiUser> userOptional(@NotNull UUID uuid) {
        if (userCache.containsKey(uuid))
            return Optional.of(userCache.get(uuid));

        Optional<AtomiUser> result;
        try {
            result = userStorage.load(uuid);
        } catch (StorageException e) {
            throw new IllegalStateException("Failed loading user " + uuid, e);
        }

        result.ifPresent(user -> userCache.put(uuid, user));
        return result;
    }

    @Override
    public boolean userExists(@NotNull UUID uuid) {
        return userStorage.exists(uuid);
    }

    @Override
    public @NotNull Optional<AtomiUser> userFromCache(@NotNull UUID uuid) {
        return Optional.ofNullable(userCache.get(uuid));
    }

    @Override
    public @NotNull Optional<AtomiGroup> group(@NotNull String name) {
        return Optional.ofNullable(groups.get(name));
    }

    @Override
    public boolean groupExists(@NotNull String name) {
        return groups.containsKey(name);
    }

    @Override
    public @NotNull AtomiGroup getOrCreateGroup(@NotNull String name) {
        if (!groupNameValidityPredicate().test(name))
            throw new IllegalArgumentException("Group name '" + name + "' contains illegal characters");
        return groups.computeIfAbsent(name, (x) -> groupFactory.create(x, new AtomiEntityDataImpl()));
    }

    @Override
    public boolean removeGroup(@NotNull String name) {
        if (name.equals(DEFAULT_GROUP_NAME))
            throw new IllegalArgumentException("Default group cannot be removed");

        boolean success = groups.remove(name) != null;

        // Remove all users from the group
        if (success) {
            for (AtomiUser user : userCache.values()) {
                if (user.group().name().equals(name)) {
                    user.setGroup(defaultGroup());
                }
            }
        }

        return success;
    }

    @Override
    public void removeGroup(@NotNull AtomiGroup group) {
        removeGroup(group.name());
    }

    @Override
    public @NotNull Set<String> groupNames() {
        return Set.copyOf(groups.keySet());
    }

    @Override
    public @NotNull Collection<AtomiGroup> groups() {
        return List.copyOf(groups.values());
    }

    @Override
    public @NotNull AtomiGroup defaultGroup() {
        return getOrCreateGroup(DEFAULT_GROUP_NAME);
    }

    public void unloadUser(@NotNull UUID uuid) {
        userCache.remove(uuid);
    }

    public void updateUser(@NotNull AtomiUser user) {
        try {
            userStorage.save(user);
        } catch (StorageException e) {
            errorLogger.accept("Failed saving user " + user.uuid() + " after update", e);
        }
    }

    public void updateGroup(@NotNull AtomiGroup group) {
        try {
            groupStorage.save(groups);
        } catch (StorageException e) {
            errorLogger.accept("Failed saving groups after update", e);
        }
    }

    public @NotNull DefaultPermissionProvider defaultPermissionProvider() {
        return defaultPermissionProvider;
    }

    @Override
    public @NotNull Predicate<String> groupNameValidityPredicate() {
        return GROUP_NAME_VALIDITY_PREDICATE;
    }

    @Override
    public @NotNull Predicate<String> permissionValidityPredicate() {
        return PERMISSION_VALIDITY_PREDICATE;
    }
}
