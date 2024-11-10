package io.github.pandier.atomi.internal;

import io.github.pandier.atomi.AtomiMetadata;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class AtomiLinkedMetadataImpl extends AtomiMetadataImpl {
    private final Supplier<Stream<AtomiMetadata>> parentSupplier;
    private final Consumer<AtomiMetadata> updateCallback;

    public AtomiLinkedMetadataImpl(@NotNull Supplier<Stream<AtomiMetadata>> parentSupplier, @NotNull Consumer<AtomiMetadata> updateCallback) {
        this.parentSupplier = parentSupplier;
        this.updateCallback = updateCallback;
    }

    public AtomiLinkedMetadataImpl(@NotNull AtomiMetadata other, @NotNull Supplier<Stream<AtomiMetadata>> parentSupplier, @NotNull Consumer<AtomiMetadata> updateCallback) {
        super(other);
        this.parentSupplier = parentSupplier;
        this.updateCallback = updateCallback;
    }

    @NotNull
    public AtomiMetadata direct() {
        return new AtomiMetadataImpl(super.prefix, super.suffix, super.color);
    }

    private <T> Optional<T> traverseParents(@NotNull Function<AtomiMetadata, Optional<T>> function) {
        return parentSupplier.get().flatMap(parent -> function.apply(parent).stream()).findFirst();
    }

    @Override
    public @NotNull Optional<Component> prefix() {
        return super.prefix().or(() -> traverseParents(AtomiMetadata::prefix));
    }

    @Override
    public @NotNull AtomiMetadata setPrefix(@Nullable Component prefix) {
        super.setPrefix(prefix);
        updateCallback.accept(this);
        return this;
    }

    @Override
    public @NotNull Optional<Component> suffix() {
        return super.suffix().or(() -> traverseParents(AtomiMetadata::suffix));
    }

    @Override
    public @NotNull AtomiMetadata setSuffix(@Nullable Component suffix) {
        super.setSuffix(suffix);
        updateCallback.accept(this);
        return this;
    }

    @Override
    public @NotNull Optional<NamedTextColor> color() {
        return super.color().or(() -> traverseParents(AtomiMetadata::color));
    }

    @Override
    public @NotNull AtomiMetadata setColor(@Nullable NamedTextColor color) {
        super.setColor(color);
        updateCallback.accept(this);
        return this;
    }
}
