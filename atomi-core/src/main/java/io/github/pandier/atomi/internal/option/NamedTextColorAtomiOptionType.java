package io.github.pandier.atomi.internal.option;

import com.google.gson.JsonElement;
import io.github.pandier.atomi.internal.command.argument.AtomiArgument;
import io.github.pandier.atomi.internal.command.argument.NamedTextColorAtomiArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public class NamedTextColorAtomiOptionType implements ArgumentableAtomiOptionType<NamedTextColor> {

    @Override
    public @NotNull Class<NamedTextColor> classType() {
        return NamedTextColor.class;
    }

    @Override
    public @NotNull Component displayText(@NotNull NamedTextColor value) {
        return Component.text(value.toString()).color(value);
    }

    @Override
    public @NotNull JsonElement serializeToJson(@NotNull NamedTextColor value) {
        return AdventureSerializers.GSON.serializer().toJsonTree(value);
    }

    @Override
    public @NotNull NamedTextColor deserializeFromJson(@NotNull JsonElement json) {
        return AdventureSerializers.GSON.serializer().fromJson(json, NamedTextColor.class);
    }

    @Override
    public @NotNull AtomiArgument<?> createArgument(@NotNull String name) {
        return new NamedTextColorAtomiArgument(name);
    }
}
