package io.github.pandier.atomi.internal;

import io.github.pandier.atomi.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@ApiStatus.Internal
public class AtomiUserImpl extends AbstractAtomiEntity implements AtomiUser {
    private final UUID uuid;
    private final AtomiUserDataImpl data;

    private final LinkedHashSet<AtomiContext> contexts = new LinkedHashSet<>();

    public AtomiUserImpl(@NotNull AbstractAtomi atomi, @NotNull UUID uuid, @NotNull AtomiUserDataImpl data) {
        super(atomi);
        this.uuid = uuid;
        this.data = data;
        this.data.setUpdateCallback((x) -> update());
    }

    protected void update() {
        atomi.updateUser(this);
    }

    @Override
    public @NotNull UUID uuid() {
        return uuid;
    }

    @Override
    public @NotNull AtomiUserData data() {
        return data;
    }

    @Override
    protected @NotNull Tristate defaultPermission(@NotNull String permission) {
        return atomi.defaultPermissionProvider().user(this, permission);
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
        parents.add(group());
        return parents;
    }
}
