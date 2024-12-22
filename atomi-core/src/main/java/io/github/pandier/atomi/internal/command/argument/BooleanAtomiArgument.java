package io.github.pandier.atomi.internal.command.argument;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public class BooleanAtomiArgument extends AtomiArgument<BooleanAtomiArgument> {

    public BooleanAtomiArgument(@NotNull String name) {
        super(name);
    }

    @Override
    protected BooleanAtomiArgument getThis() {
        return this;
    }
}
