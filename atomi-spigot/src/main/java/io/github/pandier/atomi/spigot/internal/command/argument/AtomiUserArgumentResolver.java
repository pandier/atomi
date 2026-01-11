package io.github.pandier.atomi.spigot.internal.command.argument;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.pandier.atomi.AtomiUser;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
@FunctionalInterface
public interface AtomiUserArgumentResolver {

    @NotNull AtomiUser resolve(@NotNull CommandSourceStack source) throws CommandSyntaxException;
}
