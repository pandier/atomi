package io.github.pandier.atomi;

import com.google.gson.JsonElement;
import io.github.pandier.atomi.internal.option.ComponentAtomiOptionType;
import io.github.pandier.atomi.internal.option.IntegerAtomiOptionType;
import io.github.pandier.atomi.internal.option.NamedTextColorAtomiOptionType;
import io.github.pandier.atomi.internal.option.StringAtomiOptionType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.NonExtendable
public interface AtomiOptionType<T> {
    AtomiOptionType<String> STRING = new StringAtomiOptionType();
    AtomiOptionType<Integer> INTEGER = new IntegerAtomiOptionType();
    AtomiOptionType<Component> COMPONENT = new ComponentAtomiOptionType();
    AtomiOptionType<NamedTextColor> NAMED_TEXT_COLOR = new NamedTextColorAtomiOptionType();

    @NotNull
    Class<T> classType();

    @NotNull
    Component displayText(@NotNull T value);

    @NotNull
    JsonElement serializeToJson(@NotNull T value);

    @NotNull
    T deserializeFromJson(@NotNull JsonElement json);
}
