package io.github.pandier.atomi.spigot.internal.command;

import io.github.pandier.atomi.internal.command.AtomiCommandContext;
import io.github.pandier.atomi.spigot.SpigotAtomi;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class SpigotAtomiCommandContext extends AtomiCommandContext {
    private static final LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER = LegacyComponentSerializer.builder()
            .character(LegacyComponentSerializer.SECTION_CHAR)
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    private final CommandSender sender;

    public SpigotAtomiCommandContext(@NotNull CommandSender sender, @NotNull Map<String, Object> arguments) {
        super(SpigotAtomi.get(), arguments);
        this.sender = sender;
    }

    @Override
    public void sendFeedback(@NotNull Component component) {
        sender.sendMessage(LEGACY_COMPONENT_SERIALIZER.serialize(component));
    }
}
