package io.github.pandier.atomi.spigot.internal.command;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandTree;
import io.github.pandier.atomi.AtomiUser;
import io.github.pandier.atomi.internal.command.AbstractCommand;
import io.github.pandier.atomi.internal.command.GroupCommand;
import io.github.pandier.atomi.internal.command.UserCommand;
import io.github.pandier.atomi.internal.option.AtomiOptionRegistry;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class AtomiCommands {

    @Nullable
    private static String userDisplayName(AtomiUser user) {
        return Bukkit.getOfflinePlayer(user.uuid()).getName();
    }

    private static void registerWithCommands(AbstractCommand... commands) {
        CommandTree atomi = new CommandTree("atomi")
                .withPermission("atomi.command");
        for (AbstractCommand command : commands)
            atomi.then(SpigotAtomiArgumentMapper.map(command.create()));
        atomi.register();
    }

    public static void register(AtomiOptionRegistry optionRegistry) {
        registerWithCommands(
                new UserCommand(optionRegistry, AtomiCommands::userDisplayName),
                new GroupCommand(optionRegistry)
        );
    }

    public static void unregister() {
        CommandAPI.unregister("atomi");
    }
}
