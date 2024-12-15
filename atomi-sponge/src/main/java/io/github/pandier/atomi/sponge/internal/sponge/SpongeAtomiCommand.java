package io.github.pandier.atomi.sponge.internal.sponge;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.pandier.atomi.internal.command.AbstractCommand;
import io.github.pandier.atomi.internal.command.argument.LiteralAtomiArgument;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader;
import org.spongepowered.api.command.registrar.tree.CommandTreeNode;

import java.util.List;
import java.util.Optional;

@ApiStatus.Internal
public class SpongeAtomiCommand implements Command.Raw {
    private final CommandDispatcher<CommandCause> dispatcher = new CommandDispatcher<>();
    private final CommandTreeNode.Root completionNode = (CommandTreeNode.Root) CommandTreeNode.root();

    public SpongeAtomiCommand() {
        register(new AbstractCommand() {
            @Override
            public @NotNull LiteralAtomiArgument create() {
                return new LiteralAtomiArgument("test")
                        .then(new LiteralAtomiArgument("weee")
                                .executes(ctx -> true));
            }
        });
    }

    private void register(AbstractCommand command) {
        LiteralAtomiArgument literal = command.create();
        SpongeAtomiArgumentMapper mapper = new SpongeAtomiArgumentMapper(literal);
        this.dispatcher.register((LiteralArgumentBuilder) mapper.getArgumentBuilder());
        this.completionNode.child(literal.name(), mapper.getCompletionNode());
    }

    @Override
    public CommandResult process(CommandCause cause, ArgumentReader.Mutable arguments) throws CommandException {
        try {
            int result = dispatcher.execute(arguments.remaining(), cause);
            return CommandResult.builder().result(result).build();
        } catch (CommandSyntaxException e) {
            throw new CommandException(Component.text(e.getMessage()), e);
        }
    }

    @Override
    public List<CommandCompletion> complete(CommandCause cause, ArgumentReader.Mutable arguments) throws CommandException {
        return List.of();
    }

    @Override
    public boolean canExecute(CommandCause cause) {
        return true;
    }

    @Override
    public Optional<Component> shortDescription(CommandCause cause) {
        return Optional.empty();
    }

    @Override
    public Optional<Component> extendedDescription(CommandCause cause) {
        return Optional.empty();
    }

    @Override
    public Component usage(CommandCause cause) {
        return Component.empty();
    }

    @Override
    public CommandTreeNode.Root commandTree() {
        return this.completionNode;
    }
}
