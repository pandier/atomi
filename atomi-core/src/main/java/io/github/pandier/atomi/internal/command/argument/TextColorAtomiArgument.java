package io.github.pandier.atomi.internal.command.argument;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public class TextColorAtomiArgument extends AtomiArgument<TextColorAtomiArgument> {

    public TextColorAtomiArgument(@NotNull String name) {
        super(name);
    }

    @Override
    protected TextColorAtomiArgument getThis() {
        return this;
    }
}
