package io.github.pandier.atomi.paper;

import io.github.pandier.atomi.Atomi;
import io.github.pandier.atomi.AtomiOption;
import io.github.pandier.atomi.AtomiUser;
import io.github.pandier.atomi.paper.internal.PaperAtomiPlugin;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public interface PaperAtomi extends Atomi {

    static void registerOption(@NotNull AtomiOption<?> option) {
        PaperAtomiPlugin.registerOption(option);
    }

    static boolean isAvailable() {
        return PaperAtomiPlugin.atomi != null;
    }

    @NotNull
    static PaperAtomi get() {
        if (PaperAtomiPlugin.atomi == null)
            throw new IllegalStateException("Atomi is not loaded");
        return PaperAtomiPlugin.atomi;
    }

    @NotNull
    default AtomiUser user(@NotNull OfflinePlayer player) {
        return user(player.getUniqueId());
    }
}
