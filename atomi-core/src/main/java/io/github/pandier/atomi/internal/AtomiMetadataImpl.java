package io.github.pandier.atomi.internal;

import io.github.pandier.atomi.AtomiMetadata;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@ApiStatus.Internal
public class AtomiMetadataImpl implements AtomiMetadata {
    protected Component prefix;
    protected Component suffix;
    protected NamedTextColor color;

    public AtomiMetadataImpl() {
    }

    public AtomiMetadataImpl(@NotNull AtomiMetadata other) {
        this.prefix = other.prefix().orElse(null);
        this.suffix = other.suffix().orElse(null);
        this.color = other.color().orElse(null);
    }

    public AtomiMetadataImpl(@Nullable Component prefix, @Nullable Component suffix, @Nullable NamedTextColor color) {
        this.prefix = prefix;
        this.suffix = suffix;
        this.color = color;
    }

    @Override
    public @NotNull Optional<Component> prefix() {
        return Optional.ofNullable(prefix);
    }

    @Override
    public @NotNull Optional<Component> suffix() {
        return Optional.ofNullable(suffix);
    }

    @Override
    public @NotNull Optional<NamedTextColor> color() {
        return Optional.ofNullable(color);
    }

    @Override
    public @NotNull AtomiMetadata setPrefix(@Nullable Component prefix) {
        this.prefix = prefix;
        return this;
    }

    @Override
    public @NotNull AtomiMetadata setSuffix(@Nullable Component suffix) {
        this.suffix = suffix;
        return this;
    }

    @Override
    public @NotNull AtomiMetadata setColor(@Nullable NamedTextColor color) {
        this.color = color;
        return this;
    }
}
