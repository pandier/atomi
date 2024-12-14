package io.github.pandier.atomi.spigot.internal.command;

import dev.jorel.commandapi.arguments.*;
import io.github.pandier.atomi.AtomiOptionType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@ApiStatus.Internal
public class AtomiOptionTypeArguments {
    private static final Map<Class<?>, Function<String, ? extends Argument<?>>> ARGUMENT_FACTORIES = new HashMap<>();

    static {
        register(AtomiOptionType.STRING, StringArgument::new);
        register(AtomiOptionType.INTEGER, IntegerArgument::new);
        register(AtomiOptionType.COMPONENT, AdventureChatComponentArgument::new);
        register(AtomiOptionType.NAMED_TEXT_COLOR, AdventureChatColorArgument::new);
    }

    private static <T> void register(AtomiOptionType<T> type, Function<String, ? extends Argument<T>> factory) {
        ARGUMENT_FACTORIES.put(type.getClass(), factory);
    }

    @NotNull
    public static <T> Argument<T> createArgument(@NotNull AtomiOptionType<T> type, @NotNull String name) {
        return getArgumentFactory(type).apply(name);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public static <T> Function<String, ? extends Argument<T>> getArgumentFactory(@NotNull AtomiOptionType<T> type) {
        Function<String, ? extends Argument<?>> factory = ARGUMENT_FACTORIES.get(type.getClass());
        if (factory == null)
            throw new IllegalStateException("No command argument factory for option type " + type.getClass());
        return name -> (Argument<T>) factory.apply(name);
    }

    public static boolean hasArgumentFactory(@NotNull AtomiOptionType<?> type) {
        return ARGUMENT_FACTORIES.containsKey(type.getClass());
    }
}
