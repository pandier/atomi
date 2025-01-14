package io.github.pandier.atomi.internal.option;

import com.google.gson.JsonElement;
import io.github.pandier.atomi.internal.command.argument.AtomiArgument;
import io.github.pandier.atomi.internal.command.argument.ComponentAtomiArgument;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public class ComponentAtomiOptionType implements ArgumentableAtomiOptionType<Component> {

    @Override
    public @NotNull Class<Component> classType() {
        return Component.class;
    }

    @Override
    public @NotNull Component displayText(@NotNull Component value) {
        return value;
    }

    @Override
    public @NotNull JsonElement serializeToJson(@NotNull Component value) {
        return AdventureSerializers.GSON.serializeToTree(value);
    }

    @Override
    public @NotNull Component deserializeFromJson(@NotNull JsonElement json) {
        return AdventureSerializers.GSON.deserializeFromTree(json);
    }

    @Override
    public @NotNull AtomiArgument<?> createArgument(@NotNull String name) {
        return new ComponentAtomiArgument(name);
    }
}
