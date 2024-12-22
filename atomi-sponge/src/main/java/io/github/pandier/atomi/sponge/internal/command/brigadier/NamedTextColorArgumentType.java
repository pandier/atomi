package io.github.pandier.atomi.sponge.internal.command.brigadier;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

public class NamedTextColorArgumentType implements ArgumentType<NamedTextColor> {
    public static final DynamicCommandExceptionType INVALID_COLOR_EXCEPTION =
            new DynamicCommandExceptionType(x -> new ComponentMessage(Component.translatable("argument.color.invalid")
                    .arguments(Component.text(x.toString()))));

    @NotNull
    public static NamedTextColorArgumentType namedTextColor() {
        return new NamedTextColorArgumentType();
    }

    @Override
    public NamedTextColor parse(StringReader reader) throws CommandSyntaxException {
        String name = reader.readUnquotedString();
        NamedTextColor value = NamedTextColor.NAMES.value(name);
        if (value == null)
            throw INVALID_COLOR_EXCEPTION.create(name);
        return value;
    }
}
