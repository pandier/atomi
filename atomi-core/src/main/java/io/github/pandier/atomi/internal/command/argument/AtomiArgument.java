package io.github.pandier.atomi.internal.command.argument;

import io.github.pandier.atomi.internal.command.AtomiCommandExecutor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@ApiStatus.Internal
public abstract class AtomiArgument<T extends AtomiArgument<T>> {
    private final String name;
    private final List<AtomiArgument<?>> children = new ArrayList<>();
    private AtomiCommandExecutor executor = null;

    public AtomiArgument(@NotNull String name) {
        this.name = name;
    }

    protected abstract T getThis();

    public T then(@NotNull AtomiArgument<?> argument) {
        children.add(argument);
        return getThis();
    }

    public T executes(@NotNull AtomiCommandExecutor executor) {
        this.executor = executor;
        return getThis();
    }

    @NotNull
    public String name() {
        return name;
    }

    @Nullable
    public AtomiCommandExecutor executor() {
        return executor;
    }

    @NotNull
    public List<AtomiArgument<?>> children() {
        return children;
    }
}
