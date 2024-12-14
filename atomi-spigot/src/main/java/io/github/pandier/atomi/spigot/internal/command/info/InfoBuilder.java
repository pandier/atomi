package io.github.pandier.atomi.spigot.internal.command.info;

import io.github.pandier.atomi.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@ApiStatus.Internal
public class InfoBuilder {
    private Component component = Component.empty();
    private final Set<String> permissionKeys = new HashSet<>();
    private final Set<String> metadataKeys = new HashSet<>();

    private static String entityToSource(AtomiEntity entity) {
        return switch (entity) {
            case AtomiUser ignored -> "U";
            case AtomiGroup ignored -> "G";
            case AtomiContext ignored -> "C";
            default -> "X";
        };
    }

    @NotNull
    public static Component player(@Nullable String name, @NotNull AtomiUser user) {
        InfoBuilder builder = new InfoBuilder();
        builder.append(Component.text(String.valueOf(name)))
                .append(Component.text(" (" + user.uuid() + ")").color(NamedTextColor.GRAY));

        builder.title("Group");
        builder.append(Component.text(" " + user.group().name()).color(NamedTextColor.GOLD));

        builder.title("Contexts");
        builder.append(Component.text(" "));
        if (!user.contexts().isEmpty()) {
            builder.join(user.contexts(), context -> Component.text(context.key().asString()).color(NamedTextColor.BLUE));
        } else {
            builder.append(Component.text("None").color(NamedTextColor.DARK_GRAY));
        }

        builder.metadata(user);
        builder.permissions(user);
        return builder.build();
    }

    @NotNull
    public static Component group(@NotNull AtomiGroup group) {
        InfoBuilder builder = new InfoBuilder();
        builder.append(Component.text(group.name()));
        builder.metadata(group);
        builder.permissions(group);
        return builder.build();
    }

    public <T> void join(Collection<T> values, Function<T, Component> display) {
        boolean first = true;
        for (T value : values) {
            if (!first)
                append(Component.text(", "));
            first = false;
            append(display.apply(value));
        }
    }

    public void title(String name) {
        append(Component.text("\n  " + name + ":").color(NamedTextColor.GRAY));
    }

    public void metadata(AtomiEntity entity) {
        title("Metadata");
        metadataEntries(entityToSource(entity), entity.data());
        for (AtomiEntity parent : entity.parents())
            metadataEntries(entityToSource(parent), parent.data());
        if (metadataKeys.isEmpty())
            append(Component.text("\n    Nothing to see here...").color(NamedTextColor.DARK_GRAY));
    }

    public void metadataEntries(@Nullable String source, AtomiEntityData data) {
        metadataEntry(source, "prefix", data.prefix().orElse(null));
        metadataEntry(source, "suffix", data.suffix().orElse(null));
        metadataEntry(source, "color", data.color().map(color -> Component.text(color.toString()).color(color)).orElse(null));
    }

    public void metadataEntry(@Nullable String source, String key, @Nullable Component value) {
        if (value == null || metadataKeys.contains(key)) return;
        metadataKeys.add(key);
        entry(source, key, NamedTextColor.LIGHT_PURPLE, value);
    }

    public void permissions(AtomiEntity entity) {
        title("Permissions");
        permissionEntries(entityToSource(entity), entity.data().permissions());
        for (AtomiEntity parent : entity.parents())
            permissionEntries(entityToSource(parent), parent.data().permissions());
        if (permissionKeys.isEmpty())
            append(Component.text("\n    Nothing to see here...").color(NamedTextColor.DARK_GRAY));
    }

    public void permissionEntries(@Nullable String source, Map<String, Boolean> permissions) {
        for (Map.Entry<String, Boolean> entry : permissions.entrySet())
            permissionEntry(source, entry.getKey(), entry.getValue());
    }

    public void permissionEntry(@Nullable String source, String key, boolean value) {
        if (permissionKeys.contains(key)) return;
        permissionKeys.add(key);
        entry(source, key, NamedTextColor.AQUA, Component.text(value).color(value ? NamedTextColor.GREEN : NamedTextColor.RED));
    }

    public void entry(@Nullable String source, String key, TextColor color, Component value) {
        append(Component.text("\n    "));
        append(Component.text("- ").color(NamedTextColor.DARK_GRAY));
        if (source != null)
            append(Component.text(source + " ").color(NamedTextColor.YELLOW));
        append(Component.text(key).color(color));
        append(Component.text(": ").color(NamedTextColor.GRAY));
        append(value);
    }

    public InfoBuilder append(Component component) {
        this.component = this.component.append(component);
        return this;
    }

    public Component build() {
        return component;
    }
}
