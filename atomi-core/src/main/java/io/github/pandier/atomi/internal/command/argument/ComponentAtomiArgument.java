package io.github.pandier.atomi.internal.command.argument;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public class ComponentAtomiArgument extends AtomiArgument<ComponentAtomiArgument> {

    public ComponentAtomiArgument(@NotNull String name) {
        super(name);
    }

    @Override
    protected ComponentAtomiArgument getThis() {
        return this;
    }
}
