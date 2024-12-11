package io.github.pandier.atomi.spigot.internal;

import io.github.pandier.atomi.AtomiUser;
import io.github.pandier.atomi.Tristate;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.permissions.*;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.logging.Level;

@ApiStatus.Internal
public class AtomiPermissible extends PermissibleBase {
    // We need to always subscribe to these permissions, because bukkit uses permission subscriptions to retrieve all permissibles it should broadcast to
    private static final List<String> FORCED_PERMISSION_SUBSCRIPTIONS = List.of(Server.BROADCAST_CHANNEL_ADMINISTRATIVE, Server.BROADCAST_CHANNEL_USERS);

    private final Player parent;
    private final AtomiUser user;
    private final List<PermissionAttachment> attachments = new ArrayList<>();
    private final Map<String, PermissionAttachmentInfo> attachmentPermissions = new HashMap<>();
    public final PermissibleBase previousPermissible;

    AtomiPermissible(@NotNull Player player, @NotNull AtomiUser user, PermissibleBase previousPermissible) {
        super(player);

        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(user, "user");

        this.parent = player;
        this.user = user;
        this.previousPermissible = previousPermissible;
    }

    @Override
    public boolean isPermissionSet(@NotNull String name) {
        return attachmentPermissions.containsKey(name.toLowerCase(Locale.ROOT)) || user.permission(name) != Tristate.UNSET;
    }

    @Override
    public boolean hasPermission(@NotNull String name) {
        String lowerName = name.toLowerCase(Locale.ROOT);
        if (attachmentPermissions.containsKey(lowerName))
            return attachmentPermissions.get(lowerName).getValue();

        return user.hasPermission(name);
    }

    @Override
    public boolean hasPermission(@NotNull Permission perm) {
        return hasPermission(perm.getName());
    }

    @Override
    public @NotNull PermissionAttachment addAttachment(@NotNull Plugin plugin) {
        Objects.requireNonNull(plugin, "plugin");
        if (!plugin.isEnabled())
            throw new IllegalArgumentException("Plugin " + plugin.getDescription().getFullName() + " is disabled");
        PermissionAttachment attachment = new PermissionAttachment(plugin, parent);
        attachments.add(attachment);
        recalculateAttachmentPermissions();
        return attachment;
    }

    @Override
    public @Nullable PermissionAttachment addAttachment(@NotNull Plugin plugin, int ticks) {
        PermissionAttachment attachment = addAttachment(plugin);

        if (Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, attachment::remove, ticks) == -1) {
            SpigotAtomiPlugin.atomi.plugin.getLogger().log(Level.SEVERE, "Failed to add permission attachment to " + parent + " for plugin " + plugin.getDescription().getFullName() + ": scheduler returned -1");
            attachment.remove();
            return null;
        }

        return attachment;
    }

    @Override
    public @NotNull PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value) {
        Objects.requireNonNull(name, "name");
        PermissionAttachment attachment = addAttachment(plugin);
        attachment.setPermission(name, value);
        return attachment;
    }

    @Override
    public @Nullable PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value, int ticks) {
        Objects.requireNonNull(name, "name");
        PermissionAttachment attachment = addAttachment(plugin, ticks);
        if (attachment != null)
            attachment.setPermission(name, value);
        return attachment;
    }

    @Override
    public void removeAttachment(@NotNull PermissionAttachment attachment) {
        Objects.requireNonNull(attachment, "attachment");

        if (!attachments.remove(attachment))
            throw new IllegalArgumentException("Attachment is not part of Permissible " + parent);

        PermissionRemovedExecutor removalCallback = attachment.getRemovalCallback();
        if (removalCallback != null)
            removalCallback.attachmentRemoved(attachment);
        recalculateAttachmentPermissions();
    }

    private void recalculateAttachmentPermissions() {
        clearAttachmentPermissions();
        for (PermissionAttachment attachment : attachments) {
            iteratePermissions(attachment.getPermissions(), (name, value) -> {
                attachmentPermissions.put(name.toLowerCase(Locale.ROOT), new PermissionAttachmentInfo(parent, name, attachment, value));
                Bukkit.getServer().getPluginManager().subscribeToPermission(name, parent);
            });
        }
    }

    private void iteratePermissions(Map<String, Boolean> permissions, BiConsumer<String, Boolean> consumer) {
        for (Map.Entry<String, Boolean> entry : permissions.entrySet()) {
            consumer.accept(entry.getKey(), entry.getValue());

            Permission permission = Bukkit.getPluginManager().getPermission(entry.getKey());
            if (permission != null) {
                iteratePermissions(permission.getChildren(), consumer);
            }
        }
    }

    @Override
    public void recalculatePermissions() {
        // We need to check if certain fields are null, because this method is called
        // from the constructor of the super class and some of the fields are not initialized yet

        if (parent != null) {
            for (String permission : FORCED_PERMISSION_SUBSCRIPTIONS)
                Bukkit.getPluginManager().subscribeToPermission(permission, parent);
        }

        if (attachmentPermissions != null) {
            recalculateAttachmentPermissions();
        }
    }

    @Override
    public @NotNull Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return Set.copyOf(attachmentPermissions.values());
    }

    private void clearAttachmentPermissions() {
        attachmentPermissions.forEach((name, info) -> Bukkit.getPluginManager().unsubscribeFromPermission(info.getPermission(), parent));
        attachmentPermissions.clear();
    }

    @Override
    public synchronized void clearPermissions() {
        for (String permission : FORCED_PERMISSION_SUBSCRIPTIONS)
            Bukkit.getPluginManager().unsubscribeFromPermission(permission, parent);

        clearAttachmentPermissions();
    }
}
