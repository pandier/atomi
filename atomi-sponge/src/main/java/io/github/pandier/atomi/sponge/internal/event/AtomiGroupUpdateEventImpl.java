package io.github.pandier.atomi.sponge.internal.event;

import io.github.pandier.atomi.AtomiGroup;
import io.github.pandier.atomi.sponge.event.AtomiGroupUpdateEvent;
import org.spongepowered.api.event.Cause;

public class AtomiGroupUpdateEventImpl implements AtomiGroupUpdateEvent {
    private final AtomiGroup group;
    private final Cause cause;

    public AtomiGroupUpdateEventImpl(AtomiGroup group, Cause cause) {
        this.group = group;
        this.cause = cause;
    }

    @Override
    public AtomiGroup group() {
        return group;
    }

    @Override
    public Cause cause() {
        return cause;
    }
}
