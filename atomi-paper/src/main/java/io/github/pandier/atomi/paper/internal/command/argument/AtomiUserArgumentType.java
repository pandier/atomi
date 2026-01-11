package io.github.pandier.atomi.paper.internal.command.argument;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.pandier.atomi.paper.PaperAtomi;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import io.papermc.paper.command.brigadier.argument.resolvers.PlayerProfileListResolver;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

// TODO: Make this public API
@ApiStatus.Internal
public class AtomiUserArgumentType implements CustomArgumentType<@NotNull AtomiUserArgumentResolver, @NotNull PlayerProfileListResolver> {

    @Override
    public @NotNull AtomiUserArgumentResolver parse(@NotNull StringReader reader) throws CommandSyntaxException {
        PlayerProfileListResolver profileResolver = getNativeType().parse(reader);
        return (source) -> {
            Collection<PlayerProfile> profiles = profileResolver.resolve(source);
            // TODO: Is this okay?
            PlayerProfile profile = profiles.stream().findFirst().orElseThrow();
            if (profile.getId() == null) {
                throw new IllegalStateException("Missing UUID");
            }
            return PaperAtomi.get().user(profile.getId());
        };
    }

    @Override
    public @NotNull ArgumentType<PlayerProfileListResolver> getNativeType() {
        return ArgumentTypes.playerProfiles();
    }
}
