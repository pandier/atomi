package io.github.pandier.atomi.internal.command.argument;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public class StringAtomiArgument extends AtomiArgument {
    private final Type type;

    public StringAtomiArgument(@NotNull String name, @NotNull Type type) {
        super(name);
        this.type = type;
    }

    @NotNull
    public Type type() {
        return type;
    }

    public enum Type {
        WORD,
        STRING,
        GREEDY,
    }
}
