package io.github.pandier.atomi.internal.option;

import io.github.pandier.atomi.AtomiOption;
import io.github.pandier.atomi.AtomiOptions;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@ApiStatus.Internal
public class AtomiOptionRegistry {
    private final Map<String, AtomiOption<?>> options = new HashMap<>();
    private volatile boolean locked = false;

    {
        register(AtomiOptions.PREFIX);
        register(AtomiOptions.SUFFIX);
        register(AtomiOptions.COLOR);
    }

    public void register(@NotNull AtomiOption<?> option) {
        if (locked)
            throw new IllegalStateException("Option registry is locked");
        if (options.containsKey(option.name()))
            throw new IllegalStateException("Option '" + option.name() + "' is already registered");
        options.put(option.name(), option);
    }

    @Nullable
    public AtomiOption<?> get(@NotNull String name) {
        return options.get(name);
    }

    public void forEach(Consumer<AtomiOption<?>> consumer) {
        options.values().forEach(consumer);
    }

    public void lock() {
        locked = true;
    }
}
