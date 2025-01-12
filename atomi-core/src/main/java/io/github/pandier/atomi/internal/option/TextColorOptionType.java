package io.github.pandier.atomi.internal.option;

import com.google.gson.JsonElement;
import io.github.pandier.atomi.internal.command.argument.AtomiArgument;
import io.github.pandier.atomi.internal.command.argument.TextColorAtomiArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public class TextColorOptionType implements ArgumentableAtomiOptionType<TextColor> {

    @Override
    public @NotNull Class<TextColor> classType() {
        return TextColor.class;
    }

    @Override
    public @NotNull Component displayText(@NotNull TextColor value) {
        return Component.text(value.toString()).color(value);
    }

    @Override
    public @NotNull JsonElement serializeToJson(@NotNull TextColor value) {
        return AdventureSerializers.GSON.serializer().toJsonTree(value);
    }

    @Override
    public @NotNull TextColor deserializeFromJson(@NotNull JsonElement json) {
        return AdventureSerializers.GSON.serializer().fromJson(json, TextColor.class);
    }

    @Override
    public @NotNull AtomiArgument<?> createArgument(@NotNull String name) {
        return new TextColorAtomiArgument(name);
    }
}
