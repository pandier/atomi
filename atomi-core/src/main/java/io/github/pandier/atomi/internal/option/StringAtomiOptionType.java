package io.github.pandier.atomi.internal.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.pandier.atomi.AtomiOptionType;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public class StringAtomiOptionType implements AtomiOptionType<String> {

    @Override
    public @NotNull Class<String> classType() {
        return String.class;
    }

    @Override
    public @NotNull Component displayText(@NotNull String value) {
        return Component.text(value);
    }

    @Override
    public @NotNull JsonElement serializeToJson(@NotNull String value) {
        return new JsonPrimitive(value);
    }

    @Override
    public @NotNull String deserializeFromJson(@NotNull JsonElement json) {
        return json.getAsString();
    }
}
