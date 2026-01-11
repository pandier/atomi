package io.github.pandier.atomi.paper;

import io.github.pandier.atomi.AtomiUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AtomiUserUpdateEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final AtomiUser user;

    public AtomiUserUpdateEvent(@NotNull AtomiUser user) {
        this.user = user;
    }

    public @NotNull AtomiUser getUser() {
        return user;
    }

    public @Nullable Player getPlayer() {
        return Bukkit.getPlayer(user.uuid());
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
