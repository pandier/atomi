package io.github.pandier.atomi.internal.command.argument;

import org.jetbrains.annotations.NotNull;

public class DoubleAtomiArgument extends AtomiArgument<DoubleAtomiArgument> {

    public DoubleAtomiArgument(@NotNull String name) {
        super(name);
    }

    @Override
    protected DoubleAtomiArgument getThis() {
        return this;
    }
}
