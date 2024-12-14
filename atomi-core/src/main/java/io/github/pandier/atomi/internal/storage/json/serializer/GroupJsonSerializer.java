package io.github.pandier.atomi.internal.storage.json.serializer;

import io.github.pandier.atomi.internal.option.AtomiOptionRegistry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public class GroupJsonSerializer extends EntityJsonSerializer {
    public GroupJsonSerializer(@NotNull AtomiOptionRegistry optionRegistry) {
        super(optionRegistry);
    }
}
