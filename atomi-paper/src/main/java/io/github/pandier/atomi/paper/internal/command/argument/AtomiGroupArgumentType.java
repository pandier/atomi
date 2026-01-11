package io.github.pandier.atomi.paper.internal.command.argument;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.pandier.atomi.AtomiGroup;
import io.github.pandier.atomi.paper.PaperAtomi;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

// TODO: Make this public API
@ApiStatus.Internal
public class AtomiGroupArgumentType implements CustomArgumentType<@NotNull AtomiGroup, @NotNull String> {
    private static final DynamicCommandExceptionType UNKNOWN_GROUP_EXCEPTION =
            new DynamicCommandExceptionType(x -> new LiteralMessage("Couldn't find group with the name '" + x + "'"));

    @Override
    public @NotNull AtomiGroup parse(@NotNull StringReader reader) throws CommandSyntaxException {
        String name = getNativeType().parse(reader);
        Optional<AtomiGroup> group = PaperAtomi.get().group(name);
        if (group.isEmpty())
            throw UNKNOWN_GROUP_EXCEPTION.create(name);
        return group.get();
    }

    @Override
    public @NotNull ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }

    @Override
    public <S> @NotNull CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        for (String group : PaperAtomi.get().groupNames())
            if (group.startsWith(builder.getRemaining()))
                builder.suggest(group);
        return CompletableFuture.completedFuture(builder.build());
    }
}
