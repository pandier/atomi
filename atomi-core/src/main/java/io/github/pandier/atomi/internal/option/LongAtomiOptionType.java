package io.github.pandier.atomi.internal.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.pandier.atomi.internal.command.argument.AtomiArgument;
import io.github.pandier.atomi.internal.command.argument.LongAtomiArgument;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class LongAtomiOptionType implements ArgumentableAtomiOptionType<Long> {

    @Override
    public @NotNull Class<Long> classType() {
        return Long.class;
    }

    @Override
    public @NotNull Component displayText(@NotNull Long value) {
        return Component.text(value.toString());
    }

    @Override
    public @NotNull JsonElement serializeToJson(@NotNull Long value) {
        return new JsonPrimitive(value);
    }

    @Override
    public @NotNull Long deserializeFromJson(@NotNull JsonElement json) {
        return json.getAsLong();
    }

    @Override
    public @NotNull AtomiArgument<?> createArgument(@NotNull String name) {
        return new LongAtomiArgument(name);
    }
}
