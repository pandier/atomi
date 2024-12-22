package io.github.pandier.atomi.internal.command.argument;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public class LiteralAtomiArgument extends AtomiArgument<LiteralAtomiArgument> {

    public LiteralAtomiArgument(@NotNull String literal) {
        super(literal);
    }

    @Override
    protected LiteralAtomiArgument getThis() {
        return this;
    }
}
