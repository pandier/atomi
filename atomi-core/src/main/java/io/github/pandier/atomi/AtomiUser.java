package io.github.pandier.atomi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public interface AtomiUser extends AtomiEntity {
    @NotNull
    UUID uuid();

    void setGroup(@Nullable AtomiGroup group);

    boolean setGroupByName(@Nullable String group);

    @NotNull
    AtomiGroup group();

    void assignContext(@NotNull AtomiContext context);

    void removeContext(@NotNull AtomiContext context);

    @NotNull
    Set<AtomiContext> contexts();
}
