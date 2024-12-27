package io.github.pandier.atomi.sponge.event;

import io.github.pandier.atomi.AtomiGroup;
import org.spongepowered.api.event.Event;

public interface AtomiGroupUpdateEvent extends Event {
    AtomiGroup group();
}
