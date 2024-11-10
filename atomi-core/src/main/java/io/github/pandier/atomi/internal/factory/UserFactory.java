package io.github.pandier.atomi.internal.factory;

import io.github.pandier.atomi.AtomiMetadata;
import io.github.pandier.atomi.AtomiUser;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

@FunctionalInterface
@ApiStatus.Internal
public interface UserFactory {
    @NotNull AtomiUser create(@NotNull UUID uuid, @Nullable String group, @NotNull Map<String, Boolean> permissions, @NotNull AtomiMetadata metadata);

    default @NotNull AtomiUser createDefault(@NotNull UUID uuid) {
        return create(uuid, null, Map.of(), AtomiMetadata.create());
    }
}
