package io.github.pandier.atomi.paper.internal.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.pandier.atomi.internal.command.AtomiCommandExecutor;
import io.github.pandier.atomi.internal.command.argument.*;
import io.github.pandier.atomi.paper.internal.command.argument.AtomiGroupArgumentType;
import io.github.pandier.atomi.paper.internal.command.argument.AtomiUserArgumentResolver;
import io.github.pandier.atomi.paper.internal.command.argument.AtomiUserArgumentType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class PaperAtomiArgumentMapper {
    private final AtomiArgument<?> atomiArgument;
    private final Map<String, ArgumentOverride<?>> argumentOverrides;

    private PaperAtomiArgumentMapper(AtomiArgument<?> atomiArgument, Map<String, ArgumentOverride<?>> argumentOverrides) {
        this.atomiArgument = atomiArgument;
        this.argumentOverrides = argumentOverrides;
    }

    @NotNull
    private ArgumentBuilder<CommandSourceStack, ?> createArgument() {
        return switch (atomiArgument) {
            case LiteralAtomiArgument literal -> Commands.literal(literal.name());
            case StringAtomiArgument string -> switch (string.type()) {
                case WORD -> Commands.argument(string.name(), StringArgumentType.word());
                case STRING -> Commands.argument(string.name(), StringArgumentType.string());
                case GREEDY -> Commands.argument(string.name(), StringArgumentType.greedyString());
            };
            case IntegerAtomiArgument integer -> Commands.argument(integer.name(), IntegerArgumentType.integer());
            case LongAtomiArgument longArg -> Commands.argument(longArg.name(), LongArgumentType.longArg());
            case FloatAtomiArgument floatArg -> Commands.argument(floatArg.name(), FloatArgumentType.floatArg());
            case DoubleAtomiArgument doubleArg -> Commands.argument(doubleArg.name(), DoubleArgumentType.doubleArg());
            case BooleanAtomiArgument bool -> Commands.argument(bool.name(), BoolArgumentType.bool());
            case ComponentAtomiArgument textComponent -> Commands.argument(textComponent.name(), ArgumentTypes.component());
            case NamedTextColorAtomiArgument namedTextColor -> Commands.argument(namedTextColor.name(), ArgumentTypes.namedColor());
            case TextColorAtomiArgument textColor -> Commands.argument(textColor.name(), ArgumentTypes.hexColor());
            case GroupAtomiArgument group -> Commands.argument(group.name(), new AtomiGroupArgumentType());
            case UserAtomiArgument user -> Commands.argument(user.name(), new AtomiUserArgumentType());
            default -> throw new IllegalArgumentException("Unknown argument type " + atomiArgument.getClass());
        };
    }

    @Nullable
    private ArgumentOverride<?> createArgumentOverride() {
        if (atomiArgument instanceof UserAtomiArgument) {
            return new ArgumentOverride<>(AtomiUserArgumentResolver.class, AtomiUserArgumentResolver::resolve);
        }
        return null;
    }

    @NotNull
    private Map<String, Object> resolveArgumentOverrides(@NotNull CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Map<String, Object> mappedArguments = new HashMap<>();
        for (Map.Entry<String, ArgumentOverride<?>> entry : this.argumentOverrides.entrySet()) {
            mappedArguments.put(entry.getKey(), entry.getValue().resolve(ctx, entry.getKey()));
        }
        return mappedArguments;
    }

    @NotNull
    private Command<CommandSourceStack> mapExecutor(@NotNull AtomiCommandExecutor executor) {
        return (ctx) -> {
            return executor.execute(new PaperAtomiCommandContext(ctx, resolveArgumentOverrides(ctx))) ? 1 : 0;
        };
    }

    @NotNull
    private ArgumentBuilder<CommandSourceStack, ?> map() {
        ArgumentOverride<?> argumentOverride = createArgumentOverride();
        if (argumentOverride != null)
            argumentOverrides.put(atomiArgument.name(), argumentOverride);

        ArgumentBuilder<CommandSourceStack, ?> argument = createArgument();

        for (AtomiArgument<?> child : atomiArgument.children()) {
            PaperAtomiArgumentMapper mapper = new PaperAtomiArgumentMapper(child, new HashMap<>(argumentOverrides));
            argument.then(mapper.map());
        }

        AtomiCommandExecutor executor = atomiArgument.executor();
        if (executor != null)
            argument.executes(mapExecutor(executor));

        return argument;
    }

    @NotNull
    public static ArgumentBuilder<CommandSourceStack, ?> map(@NotNull AtomiArgument<?> atomiArgument) {
        PaperAtomiArgumentMapper mapper = new PaperAtomiArgumentMapper(atomiArgument, new HashMap<>());
        return mapper.map();
    }

    public record ArgumentOverride<T>(Class<T> originalType, ArgumentOverrideResolver<T> resolver) {
        public Object resolve(@NotNull CommandContext<CommandSourceStack> ctx, @NotNull String name) throws CommandSyntaxException {
            T original = ctx.getArgument(name, originalType);
            return resolver.resolve(original, ctx.getSource());
        }
    }

    @FunctionalInterface
    public interface ArgumentOverrideResolver<T> {
        Object resolve(@NotNull T argument, @NotNull CommandSourceStack source) throws CommandSyntaxException;
    }
}
