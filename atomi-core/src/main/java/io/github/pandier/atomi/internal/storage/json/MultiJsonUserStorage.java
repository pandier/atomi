package io.github.pandier.atomi.internal.storage.json;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.pandier.atomi.AtomiUser;
import io.github.pandier.atomi.internal.AbstractAtomi;
import io.github.pandier.atomi.internal.factory.UserFactory;
import io.github.pandier.atomi.internal.storage.StorageException;
import io.github.pandier.atomi.internal.storage.UserStorage;
import io.github.pandier.atomi.internal.storage.json.serializer.UserJsonSerializer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

@ApiStatus.Internal
public class MultiJsonUserStorage implements UserStorage {
    private final UserFactory userFactory;
    private final UserJsonSerializer userSerializer;
    private final Gson gson;
    private final Path path;

    public MultiJsonUserStorage(@NotNull Path path, @NotNull AbstractAtomi atomi, @NotNull UserFactory userFactory, @NotNull Gson gson) {
        this.userFactory = userFactory;
        this.userSerializer = new UserJsonSerializer(atomi);
        this.gson = gson;
        this.path = path;
    }

    private Path getPathFor(UUID uuid) {
        return path.resolve(uuid.toString() + ".json");
    }

    @Override
    public @NotNull Optional<AtomiUser> load(@NotNull UUID uuid) throws StorageException {
        Path path = getPathFor(uuid);
        if (!Files.exists(path))
            return Optional.empty();

        String content;
        try {
            content = Files.readString(path);
        } catch (IOException e) {
            throw new StorageException("Failed reading file '" + path + "' for user " + uuid, e);
        }

        try {
            JsonObject object = gson.fromJson(content, JsonObject.class);
            AtomiUser user = userFactory.create(uuid, userSerializer.deserialize(object));
            return Optional.of(user);
        } catch (Exception e) {
            throw new StorageException("Failed deserializing file '" + path + "' for user " + uuid, e);
        }
    }

    @Override
    public void save(@NotNull AtomiUser entity) throws StorageException {
        JsonObject jsonObject;
        try {
            jsonObject = userSerializer.serialize(entity.data());
        } catch (Exception e) {
            throw new StorageException("Failed serializing user " + entity.uuid(), e);
        }

        Path path = getPathFor(entity.uuid());

        try {
            // Delete entities with empty json objects to optimize file count
            if (jsonObject.isEmpty()) {
                if (Files.exists(path))
                    Files.delete(path);
                return;
            }

            if (!Files.exists(path.getParent()))
                Files.createDirectories(path.getParent());
            Files.writeString(path, gson.toJson(jsonObject));
        } catch (IOException e) {
            throw new StorageException("Failed saving file '" + path + "' for user " + entity.uuid(), e);
        }
    }

    @Override
    public boolean exists(@NotNull UUID uuid) {
        return Files.exists(getPathFor(uuid));
    }

    @Override
    public void delete(@NotNull UUID uuid) throws StorageException {
        try {
            Files.deleteIfExists(getPathFor(uuid));
        } catch (IOException e) {
            throw new StorageException("Failed deleting file '" + getPathFor(uuid) + "' for user " + uuid, e);
        }
    }

    @Override
    public void delete(@NotNull AtomiUser entity) throws StorageException {
        delete(entity.uuid());
    }
}
