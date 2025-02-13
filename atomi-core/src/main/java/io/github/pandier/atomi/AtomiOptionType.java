package io.github.pandier.atomi;

import com.google.gson.JsonElement;
import io.github.pandier.atomi.internal.option.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.NonExtendable
public interface AtomiOptionType<T> {
    AtomiOptionType<String> STRING = new StringAtomiOptionType();
    AtomiOptionType<Integer> INTEGER = new IntegerAtomiOptionType();
    AtomiOptionType<Long> LONG = new LongAtomiOptionType();
    AtomiOptionType<Float> FLOAT = new FloatAtomiOptionType();
    AtomiOptionType<Double> DOUBLE = new DoubleAtomiOptionType();
    AtomiOptionType<Boolean> BOOLEAN = new BooleanAtomiOptionType();
    AtomiOptionType<Component> COMPONENT = new ComponentAtomiOptionType();
    AtomiOptionType<NamedTextColor> NAMED_TEXT_COLOR = new NamedTextColorAtomiOptionType();
    AtomiOptionType<TextColor> TEXT_COLOR = new TextColorOptionType();

    @NotNull
    Class<T> classType();

    @NotNull
    Component displayText(@NotNull T value);

    @NotNull
    JsonElement serializeToJson(@NotNull T value);

    @NotNull
    T deserializeFromJson(@NotNull JsonElement json);
}
