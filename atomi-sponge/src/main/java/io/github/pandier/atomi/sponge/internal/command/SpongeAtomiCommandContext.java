package io.github.pandier.atomi.sponge.internal.command;

import com.mojang.brigadier.context.CommandContext;
import io.github.pandier.atomi.internal.command.AtomiCommandContext;
import io.github.pandier.atomi.sponge.SpongeAtomi;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandCause;

import java.util.Map;

public class SpongeAtomiCommandContext extends AtomiCommandContext {
    private final CommandContext<CommandCause> ctx;

    public SpongeAtomiCommandContext(@NotNull CommandContext<CommandCause> ctx) {
        super(SpongeAtomi.get(), Map.of());
        this.ctx = ctx;
    }

    @Override
    public void sendMessage(@NotNull Component component) {
        ctx.getSource().sendMessage(component);
    }

    @Override
    public <T> T get(@NotNull String key, @NotNull Class<T> type) {
        return ctx.getArgument(key, type);
    }
}
