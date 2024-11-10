package io.github.pandier.atomi.internal.storage.json.serializer;

import com.google.gson.JsonObject;
import io.github.pandier.atomi.AtomiGroup;
import io.github.pandier.atomi.internal.factory.GroupFactory;
import org.jetbrains.annotations.NotNull;

public class GroupJsonSerializer extends EntityJsonSerializer {
    private final GroupFactory factory;

    public GroupJsonSerializer(@NotNull GroupFactory factory) {
        this.factory = factory;
    }

    @NotNull
    public JsonObject serialize(@NotNull AtomiGroup group) {
        JsonObject jsonGroup = new JsonObject();
        super.serializeEntity(jsonGroup, group);
        return jsonGroup;
    }

    @NotNull
    public AtomiGroup deserialize(@NotNull JsonObject jsonGroup, @NotNull String name) {
        return factory.create(name, deserializePermissions(jsonGroup), deserializeMetadata(jsonGroup));
    }
}
