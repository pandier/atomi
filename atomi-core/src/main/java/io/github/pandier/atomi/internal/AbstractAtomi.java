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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

@ApiStatus.Internal
public abstract class AbstractAtomi implements Atomi {
    protected static final Pattern GROUP_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_\\-.+]+$");
    protected static final Pattern PERMISSION_PATTERN = Pattern.compile("^[a-zA-Z0-9_\\-.+*]+$");

    protected final String DEFAULT_GROUP_NAME = "default";

    protected final Logger logger;
    protected final UserFactory userFactory;
    protected final GroupFactory groupFactory;
    protected final DefaultPermissionProvider defaultPermissionProvider;

    protected final UserStorage userStorage;
    protected final GroupStorage groupStorage;

    protected final Map<String, AtomiGroup> groups;
    protected final Map<UUID, AtomiUser> userCache = new HashMap<>();

    protected AbstractAtomi(Path path, Logger logger) {
        this.logger = logger;
        this.userFactory = createUserFactory();
        this.groupFactory = createGroupFactory();
        this.defaultPermissionProvider = createDefaultPermissionProvider();

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        this.userStorage = new MultiJsonUserStorage(path.resolve("users"), userFactory, gson);
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
        return (uuid, groupName, permissions, metadata) -> {
            AtomiGroup group = Optional.ofNullable(groupName).flatMap(this::group).orElse(defaultGroup());
            return new AtomiUserImpl(this, uuid, group, permissions, metadata);
        };
    }

    @NotNull
    protected GroupFactory createGroupFactory() {
        return (name, permissions, metadata) -> new AtomiGroupImpl(this, name, name.equals(DEFAULT_GROUP_NAME), permissions, metadata);
    }

    @Override
    public @NotNull Optional<AtomiUser> user(@NotNull UUID uuid) {
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
    public @NotNull AtomiUser getOrCreateUser(@NotNull UUID uuid) {
        return user(uuid).orElseGet(() -> {
            AtomiUser user = userFactory.createDefault(uuid);
            userCache.put(uuid, user);
            return user;
        });
    }

    @Override
    public @NotNull Optional<AtomiGroup> group(@NotNull String name) {
        return Optional.ofNullable(groups.get(name));
    }

    @Override
    public @NotNull AtomiGroup getOrCreateGroup(@NotNull String name) {
        if (!isValidGroupName(name))
            throw new IllegalArgumentException("Group name '" + name + "' contains illegal characters");
        return groups.computeIfAbsent(name, groupFactory::createDefault);
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
        return Collections.unmodifiableSet(groups.keySet());
    }

    @Override
    public @NotNull Collection<AtomiGroup> groups() {
        return Collections.unmodifiableCollection(groups.values());
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
            logger.log(Level.SEVERE, "Failed saving user " + user.uuid() + " after update", e);
        }
    }

    public void updateGroup(@NotNull AtomiGroup group) {
        try {
            groupStorage.save(groups);
        } catch (StorageException e) {
            logger.log(Level.SEVERE, "Failed saving groups after update", e);
        }
    }

    public @NotNull DefaultPermissionProvider defaultPermissionProvider() {
        return defaultPermissionProvider;
    }

    @Override
    public boolean isValidGroupName(@NotNull String name) {
        return GROUP_NAME_PATTERN.matcher(name).matches();
    }

    @Override
    public boolean isValidPermission(@NotNull String permission) {
        return PERMISSION_PATTERN.matcher(permission).matches();
    }
}
