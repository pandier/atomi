package io.github.pandier.atomi.spigot.internal;

import io.github.pandier.atomi.AtomiGroup;
import io.github.pandier.atomi.AtomiUser;
import io.github.pandier.atomi.Tristate;
import io.github.pandier.atomi.internal.AbstractAtomi;
import io.github.pandier.atomi.internal.DefaultPermissionProvider;
import io.github.pandier.atomi.spigot.AtomiGroupUpdateEvent;
import io.github.pandier.atomi.spigot.AtomiUserUpdateEvent;
import io.github.pandier.atomi.spigot.SpigotAtomi;
import io.github.pandier.atomi.spigot.internal.inject.AtomiPermissibleInjector;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.ServerOperator;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

@ApiStatus.Internal
public class SpigotAtomiImpl extends AbstractAtomi implements SpigotAtomi {
    protected final Plugin plugin;

    protected final Map<UUID, BukkitTask> userUpdateTasks = new ConcurrentHashMap<>();
    protected final Map<String, BukkitTask> groupUpdateTasks = new ConcurrentHashMap<>();

    SpigotAtomiImpl(Path path, Plugin plugin) {
        super(path, (msg, t) -> plugin.getLogger().log(Level.SEVERE, msg, t));
        this.plugin = plugin;
    }

    @Override
    protected @NotNull DefaultPermissionProvider createDefaultPermissionProvider() {
        return new DefaultPermissionProvider() {
            // TODO: Think about this a bit more before reaching the same conclusion
            // Not sure if we wanna use getOfflinePlayer here, because it creates a new offline player if not found
            // by doing a request to Mojang servers, which is very bad for performance
            private boolean isOp(UUID uuid) {
                return Bukkit.getServer().getOperators().stream()
                        .filter(op -> op.getUniqueId().equals(uuid))
                        .findFirst()
                        .map(ServerOperator::isOp)
                        .orElse(false);
            }

            @Override
            public Tristate user(@NotNull AtomiUser user, @NotNull String permissionName) {
                boolean op = isOp(user.uuid());
                Permission permission = Bukkit.getServer().getPluginManager().getPermission(permissionName);
                if (permission == null)
                    return Tristate.of(Permission.DEFAULT_PERMISSION.getValue(op));
                return Tristate.of(permission.getDefault().getValue(op));
            }
        };
    }

    @Override
    public boolean removeGroup(@NotNull String name) {
        boolean success = super.removeGroup(name);
        if (success) {
            // Cancel group update task if the group is removed
            BukkitTask task = groupUpdateTasks.remove(name);
            if (task != null)
                task.cancel();
        }
        return success;
    }

    @Override
    public void updateUser(@NotNull AtomiUser user) {
        // We want to send the event and save the user only once next tick
        // This fixes many issues, like recursion when modifying the user within the event,
        // and speeds up user updates by saving only once per tick
        userUpdateTasks.computeIfAbsent(user.uuid(), uuid -> Bukkit.getScheduler().runTask(plugin, () -> {
            Bukkit.getPluginManager().callEvent(new AtomiUserUpdateEvent(user));
            super.updateUser(user);
            userUpdateTasks.remove(uuid);
        }));
    }

    @Override
    public void updateGroup(@NotNull AtomiGroup group) {
        // We want to send the event and save the group only once next tick
        // This fixes many issues, like recursion when modifying the group within the event,
        // and speeds up group updates by saving only once per tick
        groupUpdateTasks.computeIfAbsent(group.name(), name -> Bukkit.getScheduler().runTask(plugin, () -> {
            Bukkit.getPluginManager().callEvent(new AtomiGroupUpdateEvent(group));
            super.updateGroup(group);
            groupUpdateTasks.remove(name);
        }));
    }

    private AtomiPermissible createPermissible(Player player, PermissibleBase previousPermissible) {
        return new AtomiPermissible(player, SpigotAtomi.get().user(player), previousPermissible);
    }

    public void initiatePlayer(@NotNull Player player) {
        AtomiPermissibleInjector.inject(player, (previous) -> createPermissible(player, previous), plugin.getLogger());
        userOptional(player.getUniqueId()); // Preload the user into cache
    }

    public void uninitiatePlayer(@NotNull Player player) {
        AtomiPermissibleInjector.uninject(player);
        unloadUser(player.getUniqueId()); // Unload user from cache
    }
}
