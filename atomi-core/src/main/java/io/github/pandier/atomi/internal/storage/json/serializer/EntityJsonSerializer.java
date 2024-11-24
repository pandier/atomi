package io.github.pandier.atomi.internal.storage.json.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.pandier.atomi.AtomiEntityData;
import io.github.pandier.atomi.internal.AtomiEntityDataImpl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@ApiStatus.Internal
public class EntityJsonSerializer {
    protected static final GsonComponentSerializer ADVENTURE_SERIALIZER = GsonComponentSerializer.gson();

    @NotNull
    public JsonObject serialize(@NotNull AtomiEntityData data) {
        JsonObject jsonEntity = new JsonObject();

        // Metadata
        JsonObject jsonMetadata = new JsonObject();
        data.prefix().ifPresent(component -> jsonMetadata.add("prefix", ADVENTURE_SERIALIZER.serializeToTree(component)));
        data.suffix().ifPresent(component -> jsonMetadata.add("suffix", ADVENTURE_SERIALIZER.serializeToTree(component)));
        data.color().ifPresent(color -> jsonMetadata.add("color", ADVENTURE_SERIALIZER.serializer().toJsonTree(color)));
        if (!jsonMetadata.isEmpty())
            jsonEntity.add("metadata", jsonMetadata);

        // Permissions
        JsonObject permissions = new JsonObject();
        for (Map.Entry<String, Boolean> entry : data.permissions().entrySet())
            permissions.addProperty(entry.getKey(), entry.getValue());
        if (!permissions.isEmpty())
            jsonEntity.add("permissions", permissions);

        return jsonEntity;
    }

    @NotNull
    public AtomiEntityDataImpl deserialize(@NotNull JsonObject jsonEntity) {
        return new AtomiEntityDataImpl(
                deserializePermissions(jsonEntity),
                deserializePrefix(jsonEntity),
                deserializeSuffix(jsonEntity),
                deserializeColor(jsonEntity)
        );
    }

    protected <T> T deserializeMetadataElement(@NotNull JsonObject jsonEntity, @NotNull String name, Function<JsonElement, T> function) {
        if (!jsonEntity.has("metadata"))
            return null;
        JsonObject jsonMetadata = jsonEntity.getAsJsonObject("metadata");
        if (!jsonMetadata.has(name))
            return null;
        return function.apply(jsonMetadata.get(name));
    }

    protected Component deserializePrefix(@NotNull JsonObject jsonEntity) {
        return deserializeMetadataElement(jsonEntity, "prefix", ADVENTURE_SERIALIZER::deserializeFromTree);
    }

    protected Component deserializeSuffix(@NotNull JsonObject jsonEntity) {
        return deserializeMetadataElement(jsonEntity, "suffix", ADVENTURE_SERIALIZER::deserializeFromTree);
    }

    protected NamedTextColor deserializeColor(@NotNull JsonObject jsonEntity) {
        return deserializeMetadataElement(jsonEntity, "color", x -> ADVENTURE_SERIALIZER.serializer().fromJson(x, NamedTextColor.class));
    }

    protected Map<String, Boolean> deserializePermissions(@NotNull JsonObject jsonEntity) {
        Map<String, Boolean> permissions = new HashMap<>();
        if (jsonEntity.has("permissions")) {
            JsonObject jsonPermissions = jsonEntity.getAsJsonObject("permissions");
            if (jsonPermissions != null) {
                for (Map.Entry<String, JsonElement> entry : jsonPermissions.entrySet()) {
                    if (entry.getValue().isJsonNull())
                        continue;
                    permissions.put(entry.getKey(), entry.getValue().getAsBoolean());
                }
            }
        }
        return permissions;
    }
}
