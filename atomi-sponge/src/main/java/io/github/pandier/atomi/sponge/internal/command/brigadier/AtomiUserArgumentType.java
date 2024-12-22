package io.github.pandier.atomi.sponge.internal.command.brigadier;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.pandier.atomi.AtomiUser;
import io.github.pandier.atomi.sponge.SpongeAtomi;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.util.Nameable;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

@ApiStatus.Internal
public class AtomiUserArgumentType implements ArgumentType<AtomiUser> {
    public static final SimpleCommandExceptionType UNKNOWN_PLAYER_EXCEPTION =
            new SimpleCommandExceptionType(new ComponentMessage(Component.translatable("argument.player.unknown")));

    @NotNull
    public static AtomiUserArgumentType atomiUser() {
        return new AtomiUserArgumentType();
    }

    @Override
    public AtomiUser parse(StringReader reader) throws CommandSyntaxException {
        int index = reader.getCursor();
        while (reader.canRead() && reader.peek() != ' ')
            reader.skip();
        String name = reader.getString().substring(index, reader.getCursor());
        GameProfile profile = Sponge.server().gameProfileManager().cache().findByName(name)
                .orElseThrow(UNKNOWN_PLAYER_EXCEPTION::create);
        return SpongeAtomi.get().user(profile.uuid());
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        Sponge.server().streamOnlinePlayers()
                .map(Nameable::name)
                .filter(x -> x.toLowerCase(Locale.ROOT).startsWith(builder.getRemainingLowerCase()))
                .forEach(builder::suggest);
        return CompletableFuture.completedFuture(builder.build());
    }
}
