package io.github.pandier.atomi;

import io.github.pandier.atomi.internal.AtomiMetadataImpl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface AtomiMetadata {
    @NotNull
    static AtomiMetadata create() {
        return new AtomiMetadataImpl();
    }

    @NotNull
    Optional<Component> prefix();

    @NotNull
    AtomiMetadata setPrefix(@Nullable Component prefix);

    @NotNull
    Optional<Component> suffix();

    @NotNull
    AtomiMetadata setSuffix(@Nullable Component suffix);

    @NotNull
    Optional<NamedTextColor> color();

    @NotNull
    AtomiMetadata setColor(@Nullable NamedTextColor color);
}
