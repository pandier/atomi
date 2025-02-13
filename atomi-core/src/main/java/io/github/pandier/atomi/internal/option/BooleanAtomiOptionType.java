package io.github.pandier.atomi.internal.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.pandier.atomi.internal.command.argument.AtomiArgument;
import io.github.pandier.atomi.internal.command.argument.BooleanAtomiArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public class BooleanAtomiOptionType implements ArgumentableAtomiOptionType<Boolean> {
    @Override
    public @NotNull Class<Boolean> classType() {
        return boolean.class;
    }

    @Override
    public @NotNull Component displayText(@NotNull Boolean value) {
        return value ? Component.text("true").color(NamedTextColor.GREEN) : Component.text("false").color(NamedTextColor.RED);
    }

    @Override
    public @NotNull JsonElement serializeToJson(@NotNull Boolean value) {
        return new JsonPrimitive(value);
    }

    @Override
    public @NotNull Boolean deserializeFromJson(@NotNull JsonElement json) {
        return json.getAsBoolean();
    }

    @Override
    public @NotNull AtomiArgument<?> createArgument(@NotNull String name) {
        return new BooleanAtomiArgument(name);
    }
}
