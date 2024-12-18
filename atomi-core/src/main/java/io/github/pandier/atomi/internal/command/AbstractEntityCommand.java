package io.github.pandier.atomi.internal.command;

import io.github.pandier.atomi.AtomiEntity;
import io.github.pandier.atomi.Tristate;
import io.github.pandier.atomi.internal.command.argument.AtomiArgument;
import io.github.pandier.atomi.internal.command.argument.BooleanAtomiArgument;
import io.github.pandier.atomi.internal.command.argument.LiteralAtomiArgument;
import io.github.pandier.atomi.internal.command.argument.StringAtomiArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@ApiStatus.Internal
public abstract class AbstractEntityCommand<E extends AtomiEntity> extends AbstractCommand {
    protected final String name;
    protected final String type;
    protected final List<LiteralAtomiArgument> subCommands = new ArrayList<>();

    protected AbstractEntityCommand(@NotNull String name, @NotNull String type) {
        this.name = name;
        this.type = type;

        entitySubCommand("permission", x -> x
                .then(new StringAtomiArgument("permission", StringAtomiArgument.Type.STRING)
                        .then(new BooleanAtomiArgument("value")
                                .executes((ctx) ->
                                        executePermission(ctx, getEntity(ctx), ctx.get("permission", String.class), Tristate.of(ctx.get("value", Boolean.class)))))
                        .then(new LiteralAtomiArgument("unset")
                                .executes(ctx ->
                                        executePermission(ctx, getEntity(ctx), ctx.get("permission", String.class), Tristate.UNSET)))));
    }

    protected boolean executePermission(AtomiCommandContext ctx, E entity, String permission, Tristate value) {
        entity.setPermission(permission, value);

        Component response = Component.text("Permission ").color(NamedTextColor.GRAY)
                .append(formatPermission(permission))
                .append(Component.text(" for " + type + " "))
                .append(display(entity));

        if (value == Tristate.UNSET) {
            response = response.append(Component.text(" unset"));
        } else {
            response = response.append(Component.text(" set to "))
                    .append(formatTristate(value));
        }

        ctx.sendMessage(response);
        return true;
    }

    protected void subCommand(@NotNull String name, @NotNull Consumer<LiteralAtomiArgument> consumer) {
        LiteralAtomiArgument argument = new LiteralAtomiArgument(name);
        consumer.accept(argument);
        subCommands.add(argument);
    }

    protected void entitySubCommand(@NotNull String name, @NotNull Consumer<AtomiArgument<?>> consumer) {
        subCommand(name, x -> {
            AtomiArgument<?> entityArgument = createEntityArgument();
            consumer.accept(entityArgument);
            x.then(entityArgument);
        });
    }

    @NotNull
    protected abstract Component display(@NotNull E entity);

    @NotNull
    protected abstract E getEntity(@NotNull AtomiCommandContext ctx);

    @NotNull
    protected abstract AtomiArgument<?> createEntityArgument();

    @Override
    public @NotNull LiteralAtomiArgument create() {
        LiteralAtomiArgument argument = new LiteralAtomiArgument(name);
        for (LiteralAtomiArgument subCommand : subCommands)
            argument.then(subCommand);
        return argument;
    }
}
