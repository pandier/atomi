package io.github.pandier.atomi.internal;

import io.github.pandier.atomi.AtomiEntityData;
import io.github.pandier.atomi.AtomiOption;
import io.github.pandier.atomi.Tristate;
import io.github.pandier.atomi.internal.permission.PermissionTree;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

@ApiStatus.Internal
public class AtomiEntityDataImpl implements AtomiEntityData {
    private final PermissionTree permissions;
    private final ConcurrentMap<AtomiOption<?>, Object> options;
    protected Consumer<AtomiEntityData> updateCallback = (x) -> {};

    public AtomiEntityDataImpl() {
        this(Map.of(), Map.of());
    }

    public AtomiEntityDataImpl(Map<String, Boolean> permissions, Map<AtomiOption<?>, Object> options) {
        this.permissions = new PermissionTree(permissions);
        this.options = new ConcurrentHashMap<>(options);
    }

    public void setUpdateCallback(Consumer<AtomiEntityData> updateCallback) {
        this.updateCallback = updateCallback;
    }

    @Override
    public @NotNull Tristate permission(@NotNull String permission) {
        return permissions.get(permission);
    }

    @Override
    public void setPermission(@NotNull String permission, @NotNull Tristate value) {
        if (!AbstractAtomi.PERMISSION_VALIDITY_PREDICATE.test(permission))
            throw new IllegalArgumentException("Permission '" + permission + "' does not match the allowed format " + AbstractAtomi.PERMISSION_PATTERN.pattern());
        permissions.set(permission, value);
        updateCallback.accept(this);
    }

    @Override
    public @NotNull Map<String, Boolean> permissions() {
        return permissions.asMap();
    }

    @Override
    public @NotNull <T> Optional<T> option(@NotNull AtomiOption<T> option) {
        Object valueObject = options.get(option);
        if (valueObject != null && !option.type().classType().isInstance(valueObject))
            throw new IllegalArgumentException("Specified option '" + option.name() + "' is of invalid type " + option.type().classType().getName());
        return Optional.ofNullable(option.type().classType().cast(valueObject));
    }

    @Override
    public <T> void setOption(@NotNull AtomiOption<T> option, @Nullable T value) {
        if (value == null) {
            options.remove(option);
        } else {
            options.put(option, value);
        }
        updateCallback.accept(this);
    }

    @Override
    public @NotNull Map<AtomiOption<?>, Object> options() {
        return Map.copyOf(options);
    }
}
