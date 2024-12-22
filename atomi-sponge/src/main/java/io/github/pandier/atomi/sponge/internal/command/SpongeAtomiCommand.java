package io.github.pandier.atomi.sponge.internal.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import io.github.pandier.atomi.internal.command.AbstractCommand;
import io.github.pandier.atomi.internal.command.GroupCommand;
import io.github.pandier.atomi.internal.command.UserCommand;
import io.github.pandier.atomi.internal.command.argument.LiteralAtomiArgument;
import io.github.pandier.atomi.sponge.internal.command.brigadier.ComponentMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader;
import org.spongepowered.api.command.registrar.tree.CommandTreeNode;
import org.spongepowered.api.profile.GameProfile;

import java.util.List;
import java.util.Optional;

@ApiStatus.Internal
public class SpongeAtomiCommand implements Command.Raw {
    private static final String PERMISSION = "atomi.command";

    private final CommandDispatcher<CommandCause> dispatcher = new CommandDispatcher<>();
    private final CommandTreeNode.Root completionTree = CommandTreeNode.root().requires(this::canExecute);

    public SpongeAtomiCommand() {
        register(new UserCommand(user -> Sponge.server().gameProfileManager().cache().findById(user.uuid()).flatMap(GameProfile::name).orElse(null)));
        register(new GroupCommand());
    }

    @SuppressWarnings("unchecked")
    private void register(AbstractCommand command) {
        LiteralAtomiArgument literal = command.create();
        SpongeAtomiArgumentMapper.Result mapResult = SpongeAtomiArgumentMapper.map(literal);
        this.dispatcher.register((LiteralArgumentBuilder<CommandCause>) mapResult.argumentBuilder());
        this.completionTree.child(literal.name(), mapResult.completionTree());
    }

    @Override
    public CommandResult process(CommandCause cause, ArgumentReader.Mutable arguments) throws CommandException {
        String command = arguments.remaining();
        try {
            int result = dispatcher.execute(command, cause);
            return CommandResult.builder().result(result).build();
        } catch (CommandSyntaxException e) {
            Component message = e.getRawMessage() instanceof ComponentMessage(Component component) ? component : Component.text(e.getRawMessage().getString());
            if (e.getInput() != null && e.getCursor() >= 0) {
                int cursor = Math.min(e.getInput().length(), e.getCursor());
                Component mutableText = Component.text("\n").color(NamedTextColor.GRAY);
                if (cursor > 10)
                    mutableText = mutableText.append(Component.text("..."));
                mutableText = mutableText.append(Component.text(e.getInput().substring(Math.max(0, cursor - 10), cursor)));
                if (cursor < e.getInput().length())
                    mutableText = mutableText.append(Component.text(e.getInput().substring(cursor)).color(NamedTextColor.RED).decorate(TextDecoration.UNDERLINED));
                mutableText = mutableText.append(Component.translatable("command.context.here").color(NamedTextColor.RED).decorate(TextDecoration.ITALIC));
                message = message.append(mutableText);
            }
            throw new CommandException(message, e);
        }
    }

    @Override
    public List<CommandCompletion> complete(CommandCause cause, ArgumentReader.Mutable arguments) {
        ParseResults<CommandCause> parseResults = dispatcher.parse(arguments.remaining(), cause);
        Suggestions suggestions = dispatcher.getCompletionSuggestions(parseResults).join();
        return suggestions.getList().stream().map(x -> CommandCompletion.of(x.getText())).toList();
    }

    @Override
    public boolean canExecute(CommandCause cause) {
        return cause.hasPermission(PERMISSION);
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
        return this.completionTree;
    }
}
