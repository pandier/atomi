package io.github.pandier.atomi.internal.option;

import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
final class AdventureSerializers {
    public static final GsonComponentSerializer GSON = GsonComponentSerializer.gson();
}
