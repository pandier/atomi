package io.github.pandier.atomi.internal.command.argument;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public class GroupAtomiArgument extends AtomiArgument<GroupAtomiArgument> {

    public GroupAtomiArgument(@NotNull String name) {
        super(name);
    }

    @Override
    protected GroupAtomiArgument getThis() {
        return this;
    }
}
