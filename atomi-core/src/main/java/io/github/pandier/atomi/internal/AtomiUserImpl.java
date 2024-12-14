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
        this.data.setUpdateCallback((x) -> update(true));
    }

    protected void update(boolean save) {
        atomi.updateUser(this, save);
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
        synchronized (contexts) {
            contexts.add(context);
        }
        update(false);
    }

    @Override
    public void removeContext(@NotNull AtomiContext context) {
        synchronized (contexts) {
            contexts.remove(context);
        }
        update(false);
    }

    @Override
    public boolean hasContext(@NotNull AtomiContext context) {
        synchronized (contexts) {
            return contexts.contains(context);
        }
    }

    @Override
    public @NotNull Set<AtomiContext> contexts() {
        synchronized (contexts) {
            return new HashSet<>(contexts);
        }
    }

    @Override
    public @NotNull List<AtomiEntity> parents() {
        List<AtomiEntity> parents;
        synchronized (contexts) {
            parents = new ArrayList<>(contexts);
        }
        parents.add(group());
        return parents;
    }
}
