package io.github.pandier.atomi.internal.storage.json.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.pandier.atomi.AtomiEntity;
import io.github.pandier.atomi.AtomiMetadata;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@ApiStatus.Internal
public abstract class EntityJsonSerializer {
    protected static final GsonComponentSerializer ADVENTURE_SERIALIZER = GsonComponentSerializer.gson();

    protected void serializeEntity(@NotNull JsonObject jsonEntity, @NotNull AtomiEntity entity) {
        JsonObject jsonMetadata = new JsonObject();
        entity.directMetadata().prefix().ifPresent(component -> jsonMetadata.add("prefix", ADVENTURE_SERIALIZER.serializeToTree(component)));
        entity.directMetadata().suffix().ifPresent(component -> jsonMetadata.add("suffix", ADVENTURE_SERIALIZER.serializeToTree(component)));
        entity.directMetadata().color().ifPresent(color -> jsonMetadata.add("color", ADVENTURE_SERIALIZER.serializer().toJsonTree(color)));
        jsonEntity.add("metadata", jsonMetadata);

        JsonObject permissions = new JsonObject();
        for (Map.Entry<String, Boolean> entry : entity.directPermissions().entrySet()) {
            permissions.addProperty(entry.getKey(), entry.getValue());
        }
        jsonEntity.add("permissions", permissions);
    }

    protected AtomiMetadata deserializeMetadata(@NotNull JsonObject jsonEntity) {
        AtomiMetadata metadata = AtomiMetadata.create();
        if (jsonEntity.has("metadata")) {
            JsonObject jsonMetadata = jsonEntity.getAsJsonObject("metadata");
            if (jsonMetadata.has("prefix"))
                metadata.setPrefix(ADVENTURE_SERIALIZER.deserializeFromTree(jsonMetadata.get("prefix")));
            if (jsonMetadata.has("suffix"))
                metadata.setSuffix(ADVENTURE_SERIALIZER.deserializeFromTree(jsonMetadata.get("suffix")));
            if (jsonMetadata.has("color"))
                metadata.setColor(ADVENTURE_SERIALIZER.serializer().fromJson(jsonMetadata.get("color"), NamedTextColor.class));
        }
        return metadata;
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
