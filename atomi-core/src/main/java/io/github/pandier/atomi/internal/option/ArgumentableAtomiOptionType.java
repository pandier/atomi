package io.github.pandier.atomi.internal.option;

import io.github.pandier.atomi.AtomiOptionType;
import io.github.pandier.atomi.internal.command.argument.AtomiArgument;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public interface ArgumentableAtomiOptionType<T> extends AtomiOptionType<T> {

    @NotNull
    AtomiArgument<?> createArgument(@NotNull String name);
}
