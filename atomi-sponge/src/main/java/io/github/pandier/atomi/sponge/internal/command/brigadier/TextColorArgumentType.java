package io.github.pandier.atomi.sponge.internal.command.brigadier;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class TextColorArgumentType implements ArgumentType<TextColor> {
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
        return Optional.ofNullable(TextColor.fromHexString(name))
                .or(() -> Optional.ofNullable(NamedTextColor.NAMES.value(name)))
                .or(() -> Optional.ofNullable(TextColor.fromHexString(TextColor.HEX_PREFIX + name)))
                .orElseThrow(() -> INVALID_COLOR_EXCEPTION.create(name));
    }
}
