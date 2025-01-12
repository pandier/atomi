package io.github.pandier.atomi.sponge.internal.command.brigadier;

import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;

public class TextColorArgumentType implements ArgumentType<TextColor> {
    private static final GsonComponentSerializer GSON = GsonComponentSerializer.gson();
    public static final DynamicCommandExceptionType INVALID_COLOR_EXCEPTION =
            new DynamicCommandExceptionType(x -> new ComponentMessage(Component.translatable("argument.color.invalid")
                    .arguments(Component.text(x.toString()))));

    @NotNull
    public static TextColorArgumentType textColor() {
        return new TextColorArgumentType();
    }

    @Override
    public TextColor parse(StringReader reader) throws CommandSyntaxException {
        String name = reader.readString();
        try {
            TextColor color = GSON.serializer().fromJson(new JsonPrimitive(name), TextColor.class);
            if (color == null)
                throw INVALID_COLOR_EXCEPTION.create(name);
            return color;
        } catch (Exception e) {
            throw INVALID_COLOR_EXCEPTION.create(name);
        }
    }
}
