package io.github.pandier.atomi.sponge.internal.event;

import io.github.pandier.atomi.AtomiUser;
import io.github.pandier.atomi.sponge.event.AtomiUserUpdateEvent;
import org.spongepowered.api.Server;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cause;

import java.util.Optional;

public class AtomiUserUpdateEventImpl implements AtomiUserUpdateEvent {
    private final AtomiUser user;
    private final Server server;
    private final Cause cause;

    public AtomiUserUpdateEventImpl(AtomiUser user, Server server, Cause cause) {
        this.user = user;
        this.server = server;
        this.cause = cause;
    }

    @Override
    public AtomiUser user() {
        return user;
    }

    @Override
    public Optional<ServerPlayer> player() {
        return server.player(user.uuid());
    }

    @Override
    public Cause cause() {
        return cause;
    }
}
