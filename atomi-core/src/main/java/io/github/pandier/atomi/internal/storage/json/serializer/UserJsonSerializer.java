package io.github.pandier.atomi.internal.storage.json.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.pandier.atomi.AtomiUser;
import io.github.pandier.atomi.internal.factory.UserFactory;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@ApiStatus.Internal
public class UserJsonSerializer extends EntityJsonSerializer {
    private final UserFactory factory;

    public UserJsonSerializer(@NotNull UserFactory factory) {
        this.factory = factory;
    }

    @NotNull
    public JsonObject serialize(@NotNull AtomiUser user) {
        JsonObject jsonUser = new JsonObject();
        super.serializeEntity(jsonUser, user);

        if (!user.group().isDefault())
            jsonUser.addProperty("group", user.group().name());
        return jsonUser;
    }

    @NotNull
    public AtomiUser deserialize(@NotNull JsonObject jsonUser, @NotNull UUID uuid) {
        JsonElement jsonGroup = jsonUser.get("group");
        String group = jsonGroup != null && !jsonGroup.isJsonNull() ? jsonGroup.getAsString() : null;
        return factory.create(uuid, group, deserializePermissions(jsonUser), deserializeMetadata(jsonUser));
    }
}
