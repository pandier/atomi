package io.github.pandier.atomi.sponge;

import io.github.pandier.atomi.Atomi;
import io.github.pandier.atomi.AtomiUser;
import io.github.pandier.atomi.sponge.internal.SpongeAtomiPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

public interface SpongeAtomi extends Atomi {
    @NotNull
    static SpongeAtomi get() {
        if (SpongeAtomiPlugin.atomi == null)
            throw new IllegalStateException("Atomi is not loaded");
        return SpongeAtomiPlugin.atomi;
    }

    @NotNull
    default AtomiUser user(@NotNull ServerPlayer player) {
        return getOrCreateUser(player.uniqueId());
    }
}
