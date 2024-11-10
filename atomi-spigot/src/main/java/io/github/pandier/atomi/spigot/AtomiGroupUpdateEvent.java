package io.github.pandier.atomi.spigot;

import io.github.pandier.atomi.AtomiGroup;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AtomiGroupUpdateEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final AtomiGroup group;

    public AtomiGroupUpdateEvent(@NotNull AtomiGroup group) {
        this.group = group;
    }

    @NotNull
    public AtomiGroup getGroup() {
        return group;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
