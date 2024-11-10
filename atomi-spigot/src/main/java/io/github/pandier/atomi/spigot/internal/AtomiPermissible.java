package io.github.pandier.atomi.spigot.internal;

import io.github.pandier.atomi.AtomiUser;
import io.github.pandier.atomi.Tristate;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

@ApiStatus.Internal
public class AtomiPermissible extends PermissibleBase {
    private final AtomiUser user;
    public final PermissibleBase previousPermissible;

    AtomiPermissible(Player player, AtomiUser user, PermissibleBase oldPermissible) {
        super(player);
        this.user = user;
        this.previousPermissible = oldPermissible;
    }

    @Override
    public boolean isPermissionSet(@NotNull String name) {
        return user.permission(name) != Tristate.UNSET;
    }

    @Override
    public boolean hasPermission(@NotNull String name) {
        return user.hasPermission(name);
    }

    @Override
    public boolean hasPermission(@NotNull Permission perm) {
        return hasPermission(perm.getName());
    }

    @Override
    public @NotNull PermissionAttachment addAttachment(@NotNull Plugin plugin) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public @Nullable PermissionAttachment addAttachment(@NotNull Plugin plugin, int ticks) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public @NotNull PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public @Nullable PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value, int ticks) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void removeAttachment(@NotNull PermissionAttachment attachment) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void recalculatePermissions() {
    }

    @Override
    public @NotNull Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return new HashSet<>(); // TODO
    }

    @Override
    public synchronized void clearPermissions() {
    }
}
