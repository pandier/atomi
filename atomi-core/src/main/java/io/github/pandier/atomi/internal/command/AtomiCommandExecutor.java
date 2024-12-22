package io.github.pandier.atomi.internal.command;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
@ApiStatus.Internal
public interface AtomiCommandExecutor {
    boolean execute(@NotNull AtomiCommandContext context);
}
