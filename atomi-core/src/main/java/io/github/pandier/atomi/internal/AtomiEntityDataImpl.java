package io.github.pandier.atomi.internal;

import io.github.pandier.atomi.AtomiEntityData;
import io.github.pandier.atomi.Tristate;
import io.github.pandier.atomi.internal.permission.PermissionTree;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@ApiStatus.Internal
public class AtomiEntityDataImpl implements AtomiEntityData {
    private final PermissionTree permissions;
    private Component prefix;
    private Component suffix;
    private NamedTextColor color;
    private Consumer<AtomiEntityData> updateCallback = (x) -> {};

    public AtomiEntityDataImpl() {
        this(Map.of(), null, null, null);
    }

    public AtomiEntityDataImpl(Map<String, Boolean> permissions, @Nullable Component prefix, @Nullable Component suffix, @Nullable NamedTextColor color) {
        this.permissions = new PermissionTree(permissions);
        this.prefix = prefix;
        this.suffix = suffix;
        this.color = color;
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
        permissions.set(permission, value);
        updateCallback.accept(this);
    }

    @Override
    public @NotNull Map<String, Boolean> permissions() {
        return permissions.asMap();
    }

    @Override
    public @NotNull Optional<Component> prefix() {
        return Optional.ofNullable(prefix);
    }

    @Override
    public void setPrefix(@Nullable Component prefix) {
        this.prefix = prefix;
        updateCallback.accept(this);
    }

    @Override
    public @NotNull Optional<Component> suffix() {
        return Optional.ofNullable(suffix);
    }

    @Override
    public void setSuffix(@Nullable Component suffix) {
        this.suffix = suffix;
        updateCallback.accept(this);
    }

    @Override
    public @NotNull Optional<NamedTextColor> color() {
        return Optional.ofNullable(color);
    }

    @Override
    public void setColor(@Nullable NamedTextColor color) {
        this.color = color;
        updateCallback.accept(this);
    }
}
