package io.github.pandier.atomi.internal.command;

import io.github.pandier.atomi.Atomi;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public abstract class AtomiCommandContext {
    private final Atomi atomi;

    public AtomiCommandContext(@NotNull Atomi atomi) {
        this.atomi = atomi;
    }

    public abstract void sendMessage(@NotNull Component component);

    public abstract <T> T get(@NotNull String key, @NotNull Class<T> type);

    @NotNull
    public Atomi atomi() {
        return atomi;
    }
}
