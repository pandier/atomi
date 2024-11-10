package io.github.pandier.atomi.internal;

import io.github.pandier.atomi.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@ApiStatus.Internal
public class AtomiUserImpl extends AbstractAtomiEntity implements AtomiUser {
    private final LinkedHashSet<AtomiContext> contexts = new LinkedHashSet<>();
    private final UUID uuid;
    private AtomiGroup group;

    public AtomiUserImpl(
            @NotNull AbstractAtomi atomi,
            @NotNull UUID uuid,
            @NotNull AtomiGroup group,
            @NotNull Map<String, Boolean> permissions,
            @NotNull AtomiMetadata metadata) {
        super(atomi, permissions, metadata);
        this.uuid = uuid;
        this.group = group;
    }

    @Override
    protected void update() {
        atomi.updateUser(this);
    }

    @Override
    public @NotNull UUID uuid() {
        return uuid;
    }

    @Override
    protected @NotNull Tristate defaultPermission(@NotNull String permission) {
        return atomi.defaultPermissionProvider().user(this, permission);
    }

    @Override
    public void setGroup(@Nullable AtomiGroup group) {
        this.group = group != null ? group : atomi.defaultGroup();
        update();
    }

    @Override
    public boolean setGroupByName(@Nullable String groupName) {
        if (groupName == null) {
            setGroup(null);
            return true;
        }

        AtomiGroup group = atomi.group(groupName).orElse(null);
        if (group == null)
            return false;

        setGroup(group);
        return true;
    }

    @Override
    public @NotNull AtomiGroup group() {
        return group;
    }

    @Override
    public void assignContext(@NotNull AtomiContext context) {
        contexts.add(context);
    }

    @Override
    public void removeContext(@NotNull AtomiContext context) {
        contexts.remove(context);
    }

    @Override
    public @NotNull Set<AtomiContext> contexts() {
        return new HashSet<>(contexts);
    }

    @Override
    public @NotNull List<AtomiEntity> parents() {
        List<AtomiEntity> parents = new ArrayList<>(contexts);
        parents.add(group);
        return parents;
    }
}
