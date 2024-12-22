package io.github.pandier.atomi.spigot.internal;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import io.github.pandier.atomi.spigot.internal.command.AtomiCommands;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class SpigotAtomiPlugin extends JavaPlugin implements Listener {
    public static SpigotAtomiImpl atomi = null;

    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this));
    }

    @Override
    public void onEnable() {
        CommandAPI.onEnable();

        atomi = new SpigotAtomiImpl(getDataFolder().toPath(), this);

        getServer().getPluginManager().registerEvents(this, this);

        // Register commands
        AtomiCommands.register();

        // Initiate all players in case of a reload
        for (Player player : getServer().getOnlinePlayers()) {
            atomi.initiatePlayer(player);
        }
    }

    @Override
    public void onDisable() {
        AtomiCommands.unregister();
        CommandAPI.onDisable();

        for (Player player : getServer().getOnlinePlayers()) {
            atomi.uninitiatePlayer(player);
        }

        atomi = null;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void handleJoin(PlayerLoginEvent event) {
        atomi.initiatePlayer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void handleQuit(PlayerQuitEvent event) {
        atomi.uninitiatePlayer(event.getPlayer());
    }
}
