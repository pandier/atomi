package io.github.pandier.atomi.internal.factory;

import io.github.pandier.atomi.AtomiGroup;
import io.github.pandier.atomi.AtomiMetadata;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@FunctionalInterface
@ApiStatus.Internal
public interface GroupFactory {
    @NotNull AtomiGroup create(@NotNull String name, @NotNull Map<String, Boolean> permissions, @NotNull AtomiMetadata metadata);

    default @NotNull AtomiGroup createDefault(@NotNull String name) {
        return create(name, Map.of(), AtomiMetadata.create());
    }
}
