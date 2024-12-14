package io.github.pandier.atomi.spigot.internal.command;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import io.github.pandier.atomi.internal.option.AtomiOptionRegistry;
import io.github.pandier.atomi.spigot.SpigotAtomi;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class Commands {
    private static final LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER = LegacyComponentSerializer.builder()
            .character(LegacyComponentSerializer.SECTION_CHAR)
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    public static final Component PREFIX = Component.empty().color(NamedTextColor.GRAY);

    public static void send(CommandSender sender, Component component, boolean prefixed) {
        if (prefixed)
            component = PREFIX.append(component);
        sender.sendMessage(LEGACY_COMPONENT_SERIALIZER.serialize(component));
    }

    public static Argument<String> createGroupArgument() {
        return new StringArgument("group")
                .includeSuggestions(ArgumentSuggestions.strings((ignored) -> SpigotAtomi.get().groupNames().toArray(new String[0])));
    }

    public static void register(AtomiOptionRegistry optionRegistry) {
        new CommandTree("atomi")
                .withPermission("atomi.command")
                .then(UserCommand.create(optionRegistry))
                .then(GroupCommand.create(optionRegistry))
                .register();
    }

    public static void unregister() {
        CommandAPI.unregister("atomi");
    }
}
