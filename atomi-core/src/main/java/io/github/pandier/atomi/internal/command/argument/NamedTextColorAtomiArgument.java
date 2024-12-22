package io.github.pandier.atomi.internal.command.argument;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public class NamedTextColorAtomiArgument extends AtomiArgument<NamedTextColorAtomiArgument> {

    public NamedTextColorAtomiArgument(@NotNull String name) {
        super(name);
    }

    @Override
    protected NamedTextColorAtomiArgument getThis() {
        return this;
    }
}
