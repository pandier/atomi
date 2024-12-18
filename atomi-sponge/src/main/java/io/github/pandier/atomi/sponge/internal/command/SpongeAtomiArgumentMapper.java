package io.github.pandier.atomi.sponge.internal.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import io.github.pandier.atomi.internal.command.AtomiCommandExecutor;
import io.github.pandier.atomi.internal.command.argument.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.registrar.tree.CommandTreeNode;
import org.spongepowered.api.command.registrar.tree.CommandTreeNodeTypes;

@ApiStatus.Internal
public class SpongeAtomiArgumentMapper {
    private final ArgumentBuilder<CommandCause, ?> argumentBuilder;
    private final CommandTreeNode.Argument<?> completionNode;

    public SpongeAtomiArgumentMapper(@NotNull AtomiArgument<?> atomiArgument) {
        ArgumentType<?> argumentType = switch (atomiArgument) {
            case LiteralAtomiArgument ignored -> null;
            case StringAtomiArgument string -> switch (string.type()) {
                case WORD -> StringArgumentType.word();
                case GREEDY -> StringArgumentType.greedyString();
                case STRING -> StringArgumentType.string();
            };
            case BooleanAtomiArgument ignored -> BoolArgumentType.bool();
            default -> throw new IllegalArgumentException("Unknown argument type " + atomiArgument.getClass());
        };

        this.argumentBuilder = argumentType == null ? LiteralArgumentBuilder.literal(atomiArgument.name()) : RequiredArgumentBuilder.argument(atomiArgument.name(), argumentType);

        this.completionNode = switch (atomiArgument) {
            case LiteralAtomiArgument ignored -> (CommandTreeNode.Basic) CommandTreeNode.literal();
            case StringAtomiArgument ignored -> CommandTreeNodeTypes.STRING.get().createNode();
            case BooleanAtomiArgument ignored -> CommandTreeNodeTypes.BOOL.get().createNode();
            case UserAtomiArgument ignored -> CommandTreeNodeTypes.GAME_PROFILE.get().createNode();
            default -> throw new IllegalArgumentException("Unknown argument type " + atomiArgument.getClass());
        };

        AtomiCommandExecutor executor = atomiArgument.executor();
        if (executor != null) {
            this.argumentBuilder.executes(ctx -> executor.execute(new SpongeAtomiCommandContext(ctx)) ? 1 : 0);
            this.completionNode.executable();
        }

        for (AtomiArgument<?> child : atomiArgument.children()) {
            SpongeAtomiArgumentMapper mapper = new SpongeAtomiArgumentMapper(child);
            this.argumentBuilder.then(mapper.getArgumentBuilder());
            this.completionNode.child(child.name(), mapper.getCompletionNode());
        }
    }

    @NotNull
    public ArgumentBuilder<CommandCause, ?> getArgumentBuilder() {
        return argumentBuilder;
    }

    @NotNull
    public CommandTreeNode.Argument<?> getCompletionNode() {
        return completionNode;
    }
}
