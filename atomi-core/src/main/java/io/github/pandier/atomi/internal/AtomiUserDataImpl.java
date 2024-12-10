package io.github.pandier.atomi.internal;

import io.github.pandier.atomi.AtomiGroup;
import io.github.pandier.atomi.AtomiUserData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

@ApiStatus.Internal
public class AtomiUserDataImpl extends AtomiEntityDataImpl implements AtomiUserData {
    private final AbstractAtomi atomi;
    private volatile AtomiGroup group;

    public AtomiUserDataImpl(AbstractAtomi atomi) {
        this(atomi, Map.of(), null, null, null, atomi.defaultGroup());
    }

    public AtomiUserDataImpl(AbstractAtomi atomi, Map<String, Boolean> permissions, Component prefix, Component suffix, NamedTextColor color, @Nullable String groupName) {
        this(atomi, permissions, prefix, suffix, color, Optional.ofNullable(groupName).flatMap(atomi::group).orElseGet(atomi::defaultGroup));
    }

    public AtomiUserDataImpl(AbstractAtomi atomi, Map<String, Boolean> permissions, Component prefix, Component suffix, NamedTextColor color, AtomiGroup group) {
        super(permissions, prefix, suffix, color);
        this.atomi = atomi;
        this.group = group;
    }

    @Override
    public @NotNull AtomiGroup group() {
        return group;
    }

    @Override
    public void setGroup(@Nullable AtomiGroup group) {
        this.group = group != null ? group : atomi.defaultGroup();
        updateCallback.accept(this);
    }

    @Override
    public boolean setGroupByName(@Nullable String name) {
        if (name == null) {
            setGroup(null);
            return true;
        }

        return atomi.group(name)
                .map(group -> {
                    setGroup(group);
                    return true;
                })
                .orElse(false);
    }
}
