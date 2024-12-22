package io.github.pandier.atomi.spigot.internal.command;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandTree;
import io.github.pandier.atomi.AtomiUser;
import io.github.pandier.atomi.internal.command.AbstractCommand;
import io.github.pandier.atomi.internal.command.GroupCommand;
import io.github.pandier.atomi.internal.command.UserCommand;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@ApiStatus.Internal
public class AtomiCommands {
    private static final List<AbstractCommand> COMMANDS = new ArrayList<>();

    static {
        COMMANDS.add(new UserCommand(AtomiCommands::userDisplayName));
        COMMANDS.add(new GroupCommand());
    }

    @Nullable
    private static String userDisplayName(AtomiUser user) {
        return Bukkit.getOfflinePlayer(user.uuid()).getName();
    }

    public static void register() {
        CommandTree atomi = new CommandTree("atomi")
                .withPermission("atomi.command");

        for (AbstractCommand command : COMMANDS)
            atomi.then(SpigotAtomiCommandMapper.map(command.create()));

        atomi.register();
    }

    public static void unregister() {
        CommandAPI.unregister("atomi");
    }
}
