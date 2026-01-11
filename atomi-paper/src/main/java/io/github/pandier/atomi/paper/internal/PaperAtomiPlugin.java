package io.github.pandier.atomi.paper.internal;

import io.github.pandier.atomi.AtomiOption;
import io.github.pandier.atomi.internal.option.AtomiOptionRegistry;
import io.github.pandier.atomi.paper.internal.command.PaperAtomiCommand;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public class PaperAtomiPlugin extends JavaPlugin implements Listener {
    private static final AtomiOptionRegistry OPTION_REGISTRY = new AtomiOptionRegistry();
    public static PaperAtomiImpl atomi = null;

    public static void registerOption(@NotNull AtomiOption<?> option) {
        OPTION_REGISTRY.register(option);
    }

    @Override
    public void onEnable() {
        atomi = new PaperAtomiImpl(this, OPTION_REGISTRY, getDataFolder().toPath());

        getServer().getPluginManager().registerEvents(this, this);

        // Register commands
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(PaperAtomiCommand.create(atomi.optionRegistry()));
        });

        // Initiate all players in case of a reload
        for (Player player : getServer().getOnlinePlayers()) {
            atomi.initiatePlayer(player);
        }
    }

    @Override
    public void onDisable() {
        for (Player player : getServer().getOnlinePlayers()) {
            atomi.uninitiatePlayer(player);
        }

        atomi = null;
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOWEST)
    private void handleLogin(PlayerLoginEvent event) {
        atomi.initiatePlayer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void handleQuit(PlayerQuitEvent event) {
        atomi.uninitiatePlayer(event.getPlayer());
    }
}
