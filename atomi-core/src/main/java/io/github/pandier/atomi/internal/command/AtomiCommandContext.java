package io.github.pandier.atomi.internal.command;

import io.github.pandier.atomi.Atomi;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

@ApiStatus.Internal
public abstract class AtomiCommandContext {
    private final Atomi atomi;
    private final Map<String, Object> arguments;

    public AtomiCommandContext(@NotNull Atomi atomi, @NotNull Map<String, Object> arguments) {
        this.atomi = atomi;
        this.arguments = arguments;
    }

    public abstract void sendFeedback(@NotNull Component component);

    public <T> Optional<T> optional(@NotNull String key, Class<T> type) {
        return Optional.ofNullable(arguments.get(key)).map(type::cast);
    }

    public <T> T get(@NotNull String key, @NotNull Class<T> type) {
        return optional(key, type).orElseThrow(() -> new IllegalArgumentException("Missing argument '" + key + "'"));
    }

    @NotNull
    public Atomi atomi() {
        return atomi;
    }
}
