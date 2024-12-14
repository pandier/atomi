package io.github.pandier.atomi.internal.storage.json.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.pandier.atomi.AtomiEntityData;
import io.github.pandier.atomi.AtomiOption;
import io.github.pandier.atomi.internal.AtomiEntityDataImpl;
import io.github.pandier.atomi.internal.option.AtomiOptionRegistry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@ApiStatus.Internal
public class EntityJsonSerializer {
    protected final AtomiOptionRegistry optionRegistry;

    public EntityJsonSerializer(@NotNull AtomiOptionRegistry optionRegistry) {
        this.optionRegistry = optionRegistry;
    }

    @NotNull
    public JsonObject serialize(@NotNull AtomiEntityData data) {
        JsonObject jsonEntity = new JsonObject();

        // Options
        JsonObject jsonOptions = new JsonObject();
        for (Map.Entry<AtomiOption<?>, Object> entry : data.options().entrySet())
            jsonOptions.add(entry.getKey().name(), serializeOption(entry.getKey(), entry.getValue()));
        if (!jsonOptions.isEmpty())
            jsonEntity.add("options", jsonOptions);

        // Permissions
        JsonObject permissions = new JsonObject();
        for (Map.Entry<String, Boolean> entry : data.permissions().entrySet())
            permissions.addProperty(entry.getKey(), entry.getValue());
        if (!permissions.isEmpty())
            jsonEntity.add("permissions", permissions);

        return jsonEntity;
    }

    protected <T> JsonElement serializeOption(@NotNull AtomiOption<T> option, @NotNull Object value) {
        if (!option.type().classType().isInstance(value))
            throw new IllegalStateException("Value of option '" + option.name() + "' is of invalid type " + value.getClass() + " (expected " + option.type().classType() + ")");
        return option.type().serializeToJson(option.type().classType().cast(value));
    }

    @NotNull
    public AtomiEntityDataImpl deserialize(@NotNull JsonObject jsonEntity) {
        return new AtomiEntityDataImpl(
                deserializePermissions(jsonEntity),
                deserializeOptions(jsonEntity)
        );
    }

    protected Map<AtomiOption<?>, Object> deserializeOptions(@NotNull JsonObject jsonEntity) {
        Map<AtomiOption<?>, Object> options = new HashMap<>();
        JsonObject jsonOptions = jsonEntity.getAsJsonObject("options");
        if (jsonOptions != null) {
            for (Map.Entry<String, JsonElement> entry : jsonOptions.entrySet()) {
                if (entry.getValue().isJsonNull())
                    continue;
                AtomiOption<?> option = optionRegistry.get(entry.getKey());
                if (option == null)
                    continue;
                options.put(option, option.type().deserializeFromJson(entry.getValue()));
            }
        }
        return options;
    }

    protected Map<String, Boolean> deserializePermissions(@NotNull JsonObject jsonEntity) {
        Map<String, Boolean> permissions = new HashMap<>();
        JsonObject jsonPermissions = jsonEntity.getAsJsonObject("permissions");
        if (jsonPermissions != null) {
            for (Map.Entry<String, JsonElement> entry : jsonPermissions.entrySet()) {
                if (entry.getValue().isJsonNull())
                    continue;
                permissions.put(entry.getKey(), entry.getValue().getAsBoolean());
            }
        }
        return permissions;
    }
}
