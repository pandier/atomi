package io.github.pandier.atomi.sponge.internal.event;

import io.github.pandier.atomi.AtomiOption;
import io.github.pandier.atomi.internal.option.AtomiOptionRegistry;
import io.github.pandier.atomi.sponge.event.AtomiRegisterOptionEvent;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Cause;

public class AtomiRegisterOptionEventImpl implements AtomiRegisterOptionEvent {
    private final AtomiOptionRegistry optionRegistry;
    private final Game game;
    private final Cause cause;

    public AtomiRegisterOptionEventImpl(AtomiOptionRegistry optionRegistry, Game game, Cause cause) {
        this.optionRegistry = optionRegistry;
        this.game = game;
        this.cause = cause;
    }

    @Override
    public void register(AtomiOption<?> option) {
        optionRegistry.register(option);
    }

    @Override
    public Cause cause() {
        return cause;
    }

    @Override
    public Game game() {
        return game;
    }
}
