package io.github.pandier.atomi.internal.command.argument;

import org.jetbrains.annotations.NotNull;

public class LongAtomiArgument extends AtomiArgument<LongAtomiArgument> {

    public LongAtomiArgument(@NotNull String name) {
        super(name);
    }

    @Override
    protected LongAtomiArgument getThis() {
        return this;
    }
}
