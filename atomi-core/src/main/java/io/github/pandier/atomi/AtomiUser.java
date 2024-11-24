package io.github.pandier.atomi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public interface AtomiUser extends AtomiEntity {
    @NotNull
    UUID uuid();

    @NotNull
    AtomiUserData data();

    @NotNull
    default AtomiGroup group() {
        return data().group();
    }

    default void setGroup(@Nullable AtomiGroup group) {
        data().setGroup(group);
    }

    default boolean setGroupByName(@Nullable String group) {
        return data().setGroupByName(group);
    }

    void assignContext(@NotNull AtomiContext context);

    void removeContext(@NotNull AtomiContext context);

    @NotNull
    Set<AtomiContext> contexts();
}
