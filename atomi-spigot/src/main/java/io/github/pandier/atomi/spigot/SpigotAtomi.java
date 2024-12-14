package io.github.pandier.atomi.spigot;

import io.github.pandier.atomi.Atomi;
import io.github.pandier.atomi.AtomiUser;
import io.github.pandier.atomi.AtomiOption;
import io.github.pandier.atomi.spigot.internal.SpigotAtomiPlugin;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public interface SpigotAtomi extends Atomi {

    static void registerOption(@NotNull AtomiOption<?> option) {
        SpigotAtomiPlugin.registerOption(option);
    }

    static boolean isAvailable() {
        return SpigotAtomiPlugin.atomi != null;
    }

    @NotNull
    static SpigotAtomi get() {
        if (SpigotAtomiPlugin.atomi == null)
            throw new IllegalStateException("Atomi is not loaded");
        return SpigotAtomiPlugin.atomi;
    }

    @NotNull
    default AtomiUser user(@NotNull OfflinePlayer player) {
        return user(player.getUniqueId());
    }
}
