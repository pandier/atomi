package io.github.pandier.atomi.sponge.event;

import io.github.pandier.atomi.AtomiOption;
import org.spongepowered.api.event.lifecycle.LifecycleEvent;

public interface AtomiRegisterOptionEvent extends LifecycleEvent {

    void register(AtomiOption<?> option);
}
