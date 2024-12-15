package io.github.pandier.atomi.internal.command;

import io.github.pandier.atomi.AtomiGroup;
import io.github.pandier.atomi.AtomiUser;
import io.github.pandier.atomi.internal.command.argument.AtomiArgument;
import io.github.pandier.atomi.internal.command.argument.GroupAtomiArgument;
import io.github.pandier.atomi.internal.command.argument.UserAtomiArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class UserCommand extends AbstractEntityCommand<AtomiUser> {
    private final Function<AtomiUser, @Nullable String> displayNameProvider;

    public UserCommand(@NotNull Function<AtomiUser, @Nullable String> displayNameProvider) {
        super("user", "user");
        this.displayNameProvider = displayNameProvider;

        entitySubCommand("group", x -> x
                .then(new GroupAtomiArgument("group")
                        .executes(ctx -> executeGroup(ctx, getEntity(ctx), ctx.get("group", AtomiGroup.class)))));
        entitySubCommand("info", x -> x
                .executes(ctx -> executeInfo(ctx, getEntity(ctx))));
    }

    protected boolean executeGroup(AtomiCommandContext ctx, AtomiUser user, AtomiGroup group) {
        user.setGroup(group);

        ctx.sendFeedback(Component.text("Group of ").color(NamedTextColor.GRAY)
                .append(display(user))
                .append(Component.text(" set to "))
                .append(formatGroup(group.name())));
        return true;
    }

    protected boolean executeInfo(AtomiCommandContext ctx, AtomiUser user) {
        ctx.sendFeedback(EntityInfoBuilder.player(displayNameProvider.apply(user), user));
        return true;
    }

    @Override
    protected @NotNull Component display(@NotNull AtomiUser entity) {
        String displayName = displayNameProvider.apply(entity);
        return Component.text(displayName != null ? displayName : entity.uuid().toString()).color(NamedTextColor.WHITE);
    }

    @Override
    protected @NotNull AtomiUser getEntity(@NotNull AtomiCommandContext ctx) {
        return ctx.get("user", AtomiUser.class);
    }

    @Override
    protected @NotNull AtomiArgument createEntityArgument() {
        return new UserAtomiArgument("user");
    }
}
