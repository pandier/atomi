package io.github.pandier.atomi.sponge.event;

import io.github.pandier.atomi.AtomiUser;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Event;

import java.util.Optional;

public interface AtomiUserUpdateEvent extends Event {

    AtomiUser user();

    Optional<ServerPlayer> player();
}
