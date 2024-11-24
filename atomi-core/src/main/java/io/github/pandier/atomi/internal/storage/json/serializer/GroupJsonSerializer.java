package io.github.pandier.atomi.internal.storage.json.serializer;

import com.google.gson.JsonObject;
import io.github.pandier.atomi.AtomiEntityData;
import io.github.pandier.atomi.internal.AtomiEntityDataImpl;
import org.jetbrains.annotations.NotNull;

public class GroupJsonSerializer extends EntityJsonSerializer {

    @NotNull
    public JsonObject serialize(@NotNull AtomiEntityData data) {
        JsonObject jsonGroup = new JsonObject();
        super.serializeEntity(jsonGroup, data);
        return jsonGroup;
    }

    @NotNull
    public AtomiEntityDataImpl deserialize(@NotNull JsonObject jsonGroup) {
        return new AtomiEntityDataImpl(
                deserializePermissions(jsonGroup),
                deserializePrefix(jsonGroup),
                deserializeSuffix(jsonGroup),
                deserializeColor(jsonGroup)
        );
    }
}
