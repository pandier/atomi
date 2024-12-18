package io.github.pandier.atomi.internal.command;

import io.github.pandier.atomi.AtomiGroup;
import io.github.pandier.atomi.internal.command.argument.AtomiArgument;
import io.github.pandier.atomi.internal.command.argument.GroupAtomiArgument;
import io.github.pandier.atomi.internal.command.argument.StringAtomiArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public class GroupCommand extends AbstractEntityCommand<AtomiGroup> {

    public GroupCommand() {
        super("group", "group");

        subCommand("create", x -> x
                .then(new StringAtomiArgument("name", StringAtomiArgument.Type.WORD)
                        .executes(ctx -> executeCreate(ctx, ctx.get("name", String.class)))));
        entitySubCommand("remove", x -> x
                .executes(ctx -> executeRemove(ctx, getEntity(ctx))));
        entitySubCommand("info", x -> x
                .executes(ctx -> executeInfo(ctx, getEntity(ctx))));
    }

    protected boolean executeRemove(AtomiCommandContext ctx, AtomiGroup group) {
        if (group.isDefault())
            throw new IllegalArgumentException("Default group cannot be removed");
        ctx.atomi().removeGroup(group);
        ctx.sendMessage(Component.text("Removed group ").color(NamedTextColor.GRAY)
                .append(Component.text(group.name()).color(NamedTextColor.WHITE)));
        return true;
    }

    protected boolean executeCreate(AtomiCommandContext ctx, String name) {
        if (ctx.atomi().groupExists(name))
            throw new IllegalArgumentException("Group with the name '" + name + "' already exists");
        AtomiGroup group = ctx.atomi().getOrCreateGroup(name);
        ctx.sendMessage(Component.text("Created group ").color(NamedTextColor.GRAY)
                .append(Component.text(group.name()).color(NamedTextColor.WHITE)));
        return true;
    }

    protected boolean executeInfo(AtomiCommandContext ctx, AtomiGroup group) {
        ctx.sendMessage(EntityInfoBuilder.group(group));
        return true;
    }

    @Override
    protected @NotNull Component display(@NotNull AtomiGroup entity) {
        return Component.text(entity.name()).color(NamedTextColor.WHITE);
    }

    @Override
    protected @NotNull AtomiGroup getEntity(@NotNull AtomiCommandContext ctx) {
        return ctx.get("group", AtomiGroup.class);
    }

    @Override
    protected @NotNull AtomiArgument<?> createEntityArgument() {
        return new GroupAtomiArgument("group");
    }
}
