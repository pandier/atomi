package io.github.pandier.atomi.paper.internal.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.pandier.atomi.AtomiUser;
import io.github.pandier.atomi.internal.command.AbstractCommand;
import io.github.pandier.atomi.internal.command.GroupCommand;
import io.github.pandier.atomi.internal.command.UserCommand;
import io.github.pandier.atomi.internal.option.AtomiOptionRegistry;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class PaperAtomiCommand {

    @Nullable
    private static String userDisplayName(AtomiUser user) {
        return Bukkit.getOfflinePlayer(user.uuid()).getName();
    }

    private static LiteralCommandNode<CommandSourceStack> createWithCommands(AbstractCommand... commands) {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("atomi")
                .requires(source -> source.getSender().hasPermission("atomi.command"));
        for (AbstractCommand command : commands)
            builder.then(PaperAtomiArgumentMapper.map(command.create()));
        return builder.build();
    }

    public static LiteralCommandNode<CommandSourceStack> create(AtomiOptionRegistry optionRegistry) {
        return createWithCommands(
                new UserCommand(optionRegistry, PaperAtomiCommand::userDisplayName),
                new GroupCommand(optionRegistry)
        );
    }
}
