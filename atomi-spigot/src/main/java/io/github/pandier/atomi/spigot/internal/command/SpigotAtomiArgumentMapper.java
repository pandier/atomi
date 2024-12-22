package io.github.pandier.atomi.spigot.internal.command;

import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.executors.ResultingCommandExecutor;
import io.github.pandier.atomi.AtomiGroup;
import io.github.pandier.atomi.internal.command.AtomiCommandExecutor;
import io.github.pandier.atomi.internal.command.argument.*;
import io.github.pandier.atomi.spigot.SpigotAtomi;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class SpigotAtomiArgumentMapper {
    private final AtomiArgument<?> atomiArgument;
    private final Map<String, Function<Object, Object>> argumentMappers;

    private SpigotAtomiArgumentMapper(AtomiArgument<?> atomiArgument, Map<String, Function<Object, Object>> argumentMappers) {
        this.atomiArgument = atomiArgument;
        this.argumentMappers = argumentMappers;
    }

    @NotNull
    private Argument<?> createArgument() {
        return switch (atomiArgument) {
            case LiteralAtomiArgument literal -> new LiteralArgument(literal.name());
            case StringAtomiArgument string -> switch (string.type()) {
                case WORD -> new StringArgument(string.name());
                case STRING -> new TextArgument(string.name());
                case GREEDY -> new GreedyStringArgument(string.name());
            };
            case IntegerAtomiArgument integer -> new IntegerArgument(integer.name());
            case LongAtomiArgument longArg -> new LongArgument(longArg.name());
            case FloatAtomiArgument floatArg -> new FloatArgument(floatArg.name());
            case DoubleAtomiArgument doubleArg -> new DoubleArgument(doubleArg.name());
            case BooleanAtomiArgument bool -> new BooleanArgument(bool.name());
            case ComponentAtomiArgument textComponent -> new AdventureChatComponentArgument(textComponent.name());
            case NamedTextColorAtomiArgument namedTextColor -> new AdventureChatColorArgument(namedTextColor.name());
            case GroupAtomiArgument group -> new StringArgument(group.name())
                    .includeSuggestions(ArgumentSuggestions.strings(x -> SpigotAtomi.get().groupNames().toArray(new String[0])));
            case UserAtomiArgument user -> new OfflinePlayerArgument(user.name());
            default -> throw new IllegalArgumentException("Unknown argument type " + atomiArgument.getClass());
        };
    }

    @Nullable
    private Function<Object, Object> createArgumentMapper() {
        return switch (atomiArgument) {
            case UserAtomiArgument ignored -> (x) -> SpigotAtomi.get().user((OfflinePlayer) x);
            case GroupAtomiArgument ignored -> (x) -> {
                AtomiGroup group = SpigotAtomi.get().group((String) x).orElse(null);
                if (group == null) throw new IllegalArgumentException("Couldn't find group with the name '" + x + "'");
                return group;
            };
            default -> null;
        };
    }

    @NotNull
    private Map<String, Object> mapArgumentValues(@NotNull Map<String, Object> arguments) {
        Map<String, Object> mappedArguments = new HashMap<>();
        for (Map.Entry<String, Object> entry : arguments.entrySet()) {
            Function<Object, Object> mapper = argumentMappers.get(entry.getKey());
            mappedArguments.put(entry.getKey(), mapper != null ? mapper.apply(entry.getValue()) : entry.getValue());
        }
        return mappedArguments;
    }

    @NotNull
    private ResultingCommandExecutor mapExecutor(@NotNull AtomiCommandExecutor executor) {
        return (sender, args) -> {
            try {
                return executor.execute(new SpigotAtomiCommandContext(sender, mapArgumentValues(args.argsMap()))) ? 1 : 0;
            } catch (IllegalArgumentException ex) {
                sender.sendMessage(ChatColor.RED + ex.getMessage());
                return 0;
            }
        };
    }

    @NotNull
    private Argument<?> map() {
        Function<Object, Object> argumentMapper = createArgumentMapper();
        if (argumentMapper != null)
            argumentMappers.put(atomiArgument.name(), argumentMapper);

        Argument<?> argument = createArgument();

        for (AtomiArgument<?> child : atomiArgument.children()) {
            SpigotAtomiArgumentMapper mapper = new SpigotAtomiArgumentMapper(child, new HashMap<>(argumentMappers));
            argument.then(mapper.map());
        }

        AtomiCommandExecutor executor = atomiArgument.executor();
        if (executor != null)
            argument.executes(mapExecutor(executor));

        return argument;
    }

    @NotNull
    public static Argument<?> map(@NotNull AtomiArgument<?> atomiArgument) {
        SpigotAtomiArgumentMapper mapper = new SpigotAtomiArgumentMapper(atomiArgument, new HashMap<>());
        return mapper.map();
    }
}
