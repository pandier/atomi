package io.github.pandier.atomi.sponge.internal.command.brigadier;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.pandier.atomi.AtomiGroup;
import io.github.pandier.atomi.sponge.SpongeAtomi;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class AtomiGroupArgumentType implements ArgumentType<AtomiGroup> {
    private static final DynamicCommandExceptionType UNKNOWN_GROUP_EXCEPTION =
            new DynamicCommandExceptionType(x -> new ComponentMessage(Component.text("Couldn't find group with the name '" + x + "'")));

    @NotNull
    public static AtomiGroupArgumentType atomiGroup() {
        return new AtomiGroupArgumentType();
    }

    @Override
    public AtomiGroup parse(StringReader reader) throws CommandSyntaxException {
        String name = reader.readUnquotedString();
        Optional<AtomiGroup> group = SpongeAtomi.get().group(name);
        if (group.isEmpty())
            throw UNKNOWN_GROUP_EXCEPTION.create(name);
        return group.get();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for (String group : SpongeAtomi.get().groupNames())
            if (group.startsWith(builder.getRemaining()))
                builder.suggest(group);
        return CompletableFuture.completedFuture(builder.build());
    }
}
