package io.github.pandier.atomi.internal.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.pandier.atomi.internal.command.argument.AtomiArgument;
import io.github.pandier.atomi.internal.command.argument.DoubleAtomiArgument;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class DoubleAtomiOptionType implements ArgumentableAtomiOptionType<Double> {

    @Override
    public @NotNull Class<Double> classType() {
        return Double.class;
    }

    @Override
    public @NotNull Component displayText(@NotNull Double value) {
        return Component.text(value.toString());
    }

    @Override
    public @NotNull JsonElement serializeToJson(@NotNull Double value) {
        return new JsonPrimitive(value);
    }

    @Override
    public @NotNull Double deserializeFromJson(@NotNull JsonElement json) {
        return json.getAsDouble();
    }

    @Override
    public @NotNull AtomiArgument<?> createArgument(@NotNull String name) {
        return new DoubleAtomiArgument(name);
    }
}
