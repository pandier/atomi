package io.github.pandier.atomi.paper.internal.command;

import com.mojang.brigadier.context.CommandContext;
import io.github.pandier.atomi.internal.command.AtomiCommandContext;
import io.github.pandier.atomi.paper.PaperAtomi;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class PaperAtomiCommandContext extends AtomiCommandContext {
    private final CommandContext<CommandSourceStack> ctx;
    private final Map<String, Object> overrides;

    public PaperAtomiCommandContext(@NotNull CommandContext<CommandSourceStack> ctx, @NotNull Map<String, Object> overrides) {
        super(PaperAtomi.get());
        this.ctx = ctx;
        this.overrides = overrides;
    }

    @Override
    public void sendMessage(@NotNull Component component) {
        ctx.getSource().getSender().sendMessage(component);
    }

    @Override
    public <T> T get(@NotNull String key, @NotNull Class<T> type) {
        if (overrides.containsKey(key)) {
            return type.cast(overrides.get(key));
        }
        return ctx.getArgument(key, type);
    }
}
