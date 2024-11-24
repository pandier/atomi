package io.github.pandier.atomi.internal.storage.json.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.pandier.atomi.AtomiGroup;
import io.github.pandier.atomi.AtomiUserData;
import io.github.pandier.atomi.internal.AbstractAtomi;
import io.github.pandier.atomi.internal.AtomiUserDataImpl;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public class UserJsonSerializer extends EntityJsonSerializer {
    private final AbstractAtomi atomi;

    public UserJsonSerializer(AbstractAtomi atomi) {
        this.atomi = atomi;
    }

    @NotNull
    public JsonObject serialize(@NotNull AtomiUserData data) {
        JsonObject jsonUser = super.serialize(data);

        AtomiGroup group = data.group();
        if (!group.isDefault())
            jsonUser.addProperty("group", group.name());

        return jsonUser;
    }

    protected String deserializeGroup(@NotNull JsonObject jsonUser) {
        JsonElement jsonGroup = jsonUser.get("group");
        return jsonGroup != null && !jsonGroup.isJsonNull() ? jsonGroup.getAsString() : null;
    }

    @NotNull
    public AtomiUserDataImpl deserialize(@NotNull JsonObject jsonUser) {
        return new AtomiUserDataImpl(
                atomi,
                deserializePermissions(jsonUser),
                deserializePrefix(jsonUser),
                deserializeSuffix(jsonUser),
                deserializeColor(jsonUser),
                deserializeGroup(jsonUser)
        );
    }
}
