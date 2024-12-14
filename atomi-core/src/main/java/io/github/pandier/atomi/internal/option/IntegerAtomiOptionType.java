package io.github.pandier.atomi.internal.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.pandier.atomi.AtomiOptionType;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public class IntegerAtomiOptionType implements AtomiOptionType<Integer> {

    @Override
    public @NotNull Class<Integer> classType() {
        return Integer.class;
    }

    @Override
    public @NotNull Component displayText(@NotNull Integer value) {
        return Component.text(value.toString());
    }

    @Override
    public @NotNull JsonElement serializeToJson(@NotNull Integer value) {
        return new JsonPrimitive(value);
    }

    @Override
    public @NotNull Integer deserializeFromJson(@NotNull JsonElement json) {
        return json.getAsInt();
    }
}
