package io.github.pandier.atomi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface AtomiUserData extends AtomiEntityData {
    @NotNull
    AtomiGroup group();

    void setGroup(@Nullable AtomiGroup group);

    boolean setGroupByName(@Nullable String group);
}
