package io.github.pandier.atomi.sponge.internal.command.brigadier;

import com.mojang.brigadier.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public record ComponentMessage(@NotNull Component component) implements Message {
    private static final PlainTextComponentSerializer PLAIN_TEXT_COMPONENT_SERIALIZER = PlainTextComponentSerializer.plainText();

    @Override
    public String getString() {
        return PLAIN_TEXT_COMPONENT_SERIALIZER.serialize(component);
    }
}
