package io.github.pandier.atomi.spigot;

import io.github.pandier.atomi.Atomi;
import io.github.pandier.atomi.AtomiUser;
import io.github.pandier.atomi.spigot.internal.SpigotAtomiPlugin;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public interface SpigotAtomi extends Atomi {
    @NotNull
    static SpigotAtomi get() {
        if (SpigotAtomiPlugin.atomi == null)
            throw new IllegalStateException("Atomi is not loaded");
        return SpigotAtomiPlugin.atomi;
    }

    @NotNull
    default AtomiUser user(@NotNull OfflinePlayer player) {
        return getOrCreateUser(player.getUniqueId());
    }
}
