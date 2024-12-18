package io.github.pandier.atomi.sponge.internal.command.brigadier;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.pandier.atomi.AtomiUser;
import io.github.pandier.atomi.sponge.SpongeAtomi;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.profile.GameProfile;

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
        if (reader.canRead() && reader.peek() == '@') {
            // TODO
            throw new SimpleCommandExceptionType(new ComponentMessage(Component.text("Selectors are not yet supported"))).create();
        }
        int index = reader.getCursor();
        while (reader.canRead() && reader.peek() != ' ')
            reader.skip();
        String name = reader.getString().substring(index, reader.getCursor());
        GameProfile profile = Sponge.server().gameProfileManager().cache().findByName(name)
                .orElseThrow(UNKNOWN_PLAYER_EXCEPTION::create);
        return SpongeAtomi.get().user(profile.uuid());
    }
}
