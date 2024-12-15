package io.github.pandier.atomi.internal.command.argument;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public class UserAtomiArgument extends AtomiArgument<UserAtomiArgument> {

    public UserAtomiArgument(@NotNull String name) {
        super(name);
    }

    @Override
    protected UserAtomiArgument getThis() {
        return this;
    }
}
