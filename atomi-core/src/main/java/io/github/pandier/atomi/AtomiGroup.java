package io.github.pandier.atomi;

import org.jetbrains.annotations.NotNull;

public interface AtomiGroup extends AtomiEntity {
    @NotNull
    String name();

    boolean isDefault();
}
