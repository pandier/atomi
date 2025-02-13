package io.github.pandier.atomi.internal.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.pandier.atomi.internal.command.argument.AtomiArgument;
import io.github.pandier.atomi.internal.command.argument.FloatAtomiArgument;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class FloatAtomiOptionType implements ArgumentableAtomiOptionType<Float> {

    @Override
    public @NotNull AtomiArgument<?> createArgument(@NotNull String name) {
        return new FloatAtomiArgument(name);
    }

    @Override
    public @NotNull Class<Float> classType() {
        return Float.class;
    }

    @Override
    public @NotNull Component displayText(@NotNull Float value) {
        return Component.text(value.toString());
    }

    @Override
    public @NotNull JsonElement serializeToJson(@NotNull Float value) {
        return new JsonPrimitive(value);
    }

    @Override
    public @NotNull Float deserializeFromJson(@NotNull JsonElement json) {
        return json.getAsFloat();
    }
}
