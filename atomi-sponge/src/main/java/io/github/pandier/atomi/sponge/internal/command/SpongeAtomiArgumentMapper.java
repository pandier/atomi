package io.github.pandier.atomi.sponge.internal.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import io.github.pandier.atomi.internal.command.AtomiCommandExecutor;
import io.github.pandier.atomi.internal.command.argument.*;
import io.github.pandier.atomi.sponge.internal.command.brigadier.AtomiGroupArgumentType;
import io.github.pandier.atomi.sponge.internal.command.brigadier.AtomiUserArgumentType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.registrar.tree.CommandTreeNode;
import org.spongepowered.api.command.registrar.tree.CommandTreeNodeTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@ApiStatus.Internal
public class SpongeAtomiArgumentMapper {
    private static final Map<Class<?>, Function<AtomiArgument<?>, Result>> ARGUMENT_TYPES = new HashMap<>();

    static {
        register(LiteralAtomiArgument.class, x -> LiteralArgumentBuilder.literal(x.name()), x -> (CommandTreeNode.Basic) CommandTreeNode.literal());
        registerWithType(StringAtomiArgument.class, x -> switch (x.type()) {
            case WORD -> StringArgumentType.word();
            case GREEDY -> StringArgumentType.greedyString();
            case STRING -> StringArgumentType.string();
        }, x -> switch (x.type()) {
            case WORD -> CommandTreeNodeTypes.STRING.get().createNode().word();
            case GREEDY -> CommandTreeNodeTypes.STRING.get().createNode().greedy();
            case STRING -> CommandTreeNodeTypes.STRING.get().createNode();
        });
        registerWithType(BooleanAtomiArgument.class, x -> BoolArgumentType.bool(),x -> CommandTreeNodeTypes.BOOL.get().createNode());
        registerWithType(UserAtomiArgument.class, x -> AtomiUserArgumentType.atomiUser(), x -> CommandTreeNodeTypes.STRING.get().createNode().word().customCompletions());
        registerWithType(GroupAtomiArgument.class, x -> AtomiGroupArgumentType.atomiGroup(), x -> CommandTreeNodeTypes.STRING.get().createNode().word().customCompletions());
    }

    private static <T extends AtomiArgument<T>> void registerWithType(
            Class<T> clazz,
            Function<T, ArgumentType<?>> argumentTypeFactory,
            Function<T, CommandTreeNode.Argument<?>> completionTreeFactory
    ) {
        register(clazz, x -> RequiredArgumentBuilder.argument(x.name(), argumentTypeFactory.apply(x)), completionTreeFactory);
    }

    private static <T extends AtomiArgument<T>> void register(
            Class<T> clazz,
            Function<T, ArgumentBuilder<CommandCause, ?>> argumentBuilderFactory,
            Function<T, CommandTreeNode.Argument<?>> completionTreeFactory
    ) {
        ARGUMENT_TYPES.put(clazz, x -> {
            @SuppressWarnings("unchecked") T argument = (T) x;
            return new Result(argumentBuilderFactory.apply(argument), completionTreeFactory.apply(argument));
        });
    }

    private static Optional<Result> createResult(AtomiArgument<?> atomiArgument) {
        return Optional.ofNullable(ARGUMENT_TYPES.get(atomiArgument.getClass()))
                .map(x -> x.apply(atomiArgument));
    }

    @NotNull
    public static Result map(@NotNull AtomiArgument<?> atomiArgument) {
        Result result = createResult(atomiArgument).orElseThrow(() -> new IllegalArgumentException("Unknown argument type " + atomiArgument.getClass()));

        ArgumentBuilder<CommandCause, ?> argumentBuilder = result.argumentBuilder();
        CommandTreeNode.Argument<?> completionTree = result.completionTree();

        AtomiCommandExecutor executor = atomiArgument.executor();
        if (executor != null) {
            argumentBuilder.executes(ctx -> executor.execute(new SpongeAtomiCommandContext(ctx)) ? 1 : 0);
            completionTree.executable();
        }

        for (AtomiArgument<?> child : atomiArgument.children()) {
            Result childResult = SpongeAtomiArgumentMapper.map(child);
            argumentBuilder.then(childResult.argumentBuilder());
            completionTree.child(child.name(), childResult.completionTree());
        }

        return new Result(argumentBuilder, completionTree);
    }

    public record Result(
            @NotNull ArgumentBuilder<CommandCause, ?> argumentBuilder,
            @NotNull CommandTreeNode.Argument<?> completionTree
    ) {
    }
}
