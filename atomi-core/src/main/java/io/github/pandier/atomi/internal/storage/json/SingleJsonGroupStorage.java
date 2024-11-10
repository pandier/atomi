package io.github.pandier.atomi.internal.storage.json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.pandier.atomi.AtomiGroup;
import io.github.pandier.atomi.internal.factory.GroupFactory;
import io.github.pandier.atomi.internal.storage.GroupStorage;
import io.github.pandier.atomi.internal.storage.StorageException;
import io.github.pandier.atomi.internal.storage.json.serializer.GroupJsonSerializer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@ApiStatus.Internal
public class SingleJsonGroupStorage implements GroupStorage {
    private final GroupJsonSerializer groupSerializer;
    private final Path path;
    private final Gson gson;

    public SingleJsonGroupStorage(@NotNull Path path, @NotNull GroupFactory groupFactory, @NotNull Gson gson) {
        this.groupSerializer = new GroupJsonSerializer(groupFactory);
        this.path = path;
        this.gson = gson;
    }

    @Override
    public @NotNull Map<String, AtomiGroup> load() throws StorageException {
        if (!Files.exists(path))
            return new HashMap<>();

        Map<String, AtomiGroup> groups = new HashMap<>();

        String content;
        try {
            content = Files.readString(path);
        } catch (IOException e) {
            throw new StorageException("Failed reading file '" + path + "'", e);
        }

        try {
            JsonObject object = gson.fromJson(content, JsonObject.class);
            for (Map.Entry<String, JsonElement> groupEntry : object.entrySet()) {
                JsonObject jsonGroup = groupEntry.getValue().getAsJsonObject();
                AtomiGroup group = groupSerializer.deserialize(jsonGroup, groupEntry.getKey());
                groups.put(groupEntry.getKey(), group);
            }
        } catch (Exception e) {
            throw new StorageException("Failed deserializing file '" + path + "'", e);
        }

        return groups;
    }

    @Override
    public void save(Map<String, AtomiGroup> groups) throws StorageException {
        JsonObject jsonObject = new JsonObject();
        try {
            for (Map.Entry<String, AtomiGroup> entry : groups.entrySet()) {
                JsonObject jsonGroup = groupSerializer.serialize(entry.getValue());
                jsonObject.add(entry.getKey(), jsonGroup);
            }
        } catch (Exception e) {
            throw new StorageException("Failed serializing groups", e);
        }

        try {
            if (!Files.exists(path.getParent()))
                Files.createDirectories(path.getParent());
            Files.writeString(path, gson.toJson(jsonObject));
        } catch (IOException e) {
            throw new StorageException("Failed saving file '" + path + "'", e);
        }
    }
}
