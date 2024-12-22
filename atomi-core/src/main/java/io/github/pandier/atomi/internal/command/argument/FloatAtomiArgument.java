package io.github.pandier.atomi.internal.command.argument;

import org.jetbrains.annotations.NotNull;

public class FloatAtomiArgument extends AtomiArgument<FloatAtomiArgument> {

    public FloatAtomiArgument(@NotNull String name) {
        super(name);
    }

    @Override
    protected FloatAtomiArgument getThis() {
        return this;
    }
}
