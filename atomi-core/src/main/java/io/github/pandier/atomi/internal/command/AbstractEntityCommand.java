package io.github.pandier.atomi.internal.command;

import io.github.pandier.atomi.AtomiEntity;
import io.github.pandier.atomi.AtomiOption;
import io.github.pandier.atomi.Tristate;
import io.github.pandier.atomi.internal.AbstractAtomi;
import io.github.pandier.atomi.internal.command.argument.*;
import io.github.pandier.atomi.internal.option.ArgumentableAtomiOptionType;
import io.github.pandier.atomi.internal.option.AtomiOptionRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@ApiStatus.Internal
public abstract class AbstractEntityCommand<E extends AtomiEntity> extends AbstractCommand {
    protected final String name;
    protected final String type;
    protected final List<LiteralAtomiArgument> subCommands = new ArrayList<>();

    protected AbstractEntityCommand(@NotNull String name, @NotNull String type, @NotNull AtomiOptionRegistry optionRegistry) {
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
        entitySubCommand("option", x -> optionRegistry.forEach(option -> {
            AtomiArgument<?> argument = createOptionArgument(option);
            if (argument != null)
                x.then(argument);
        }));
    }

    @Nullable
    protected <T> AtomiArgument<?> createOptionArgument(AtomiOption<T> option) {
        if (!(option.type() instanceof ArgumentableAtomiOptionType<T> argumentableType))
            return null;
        return new LiteralAtomiArgument(option.name())
                .then(new LiteralAtomiArgument("set")
                        .then(argumentableType.createArgument("value")
                                .executes((ctx) -> executeSetOption(ctx, getEntity(ctx), option, ctx.get("value", option.type().classType())))))
                .then(new LiteralAtomiArgument("unset")
                        .executes(ctx -> executeSetOption(ctx, getEntity(ctx), option, null)));
    }

    protected boolean executePermission(AtomiCommandContext ctx, E entity, String permission, Tristate value) {
        if (!AbstractAtomi.PERMISSION_VALIDITY_PREDICATE.test(permission)) {
            ctx.sendMessage(Component.text("Permission '" + permission + "' does not match the allowed format " + AbstractAtomi.PERMISSION_PATTERN.pattern()).color(NamedTextColor.RED));
            return false;
        }

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

    protected <T> boolean executeSetOption(AtomiCommandContext ctx, E entity, AtomiOption<T> option, @Nullable T value) {
        entity.setOption(option, value);

        Component response = Component.text("Option ").color(NamedTextColor.GRAY)
                .append(formatOption(option.name()))
                .append(Component.text(" for " + type + " "))
                .append(display(entity));

        if (value == null) {
            response = response.append(Component.text(" unset"));
        } else {
            response = response.append(Component.text(" set to "))
                    .append(Component.empty().color(NamedTextColor.WHITE)
                            .append(option.type().displayText(value)));
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
