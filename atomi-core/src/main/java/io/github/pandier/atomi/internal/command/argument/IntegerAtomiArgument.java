package io.github.pandier.atomi.internal.command.argument;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public class IntegerAtomiArgument extends AtomiArgument<IntegerAtomiArgument> {

    public IntegerAtomiArgument(@NotNull String name) {
        super(name);
    }

    @Override
    protected IntegerAtomiArgument getThis() {
        return this;
    }
}
