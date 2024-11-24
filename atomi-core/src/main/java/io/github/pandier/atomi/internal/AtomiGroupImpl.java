package io.github.pandier.atomi.internal;

import io.github.pandier.atomi.AtomiEntity;
import io.github.pandier.atomi.AtomiEntityData;
import io.github.pandier.atomi.AtomiGroup;
import io.github.pandier.atomi.Tristate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@ApiStatus.Internal
public class AtomiGroupImpl extends AbstractAtomiEntity implements AtomiGroup {
    private final String name;
    private final boolean isDefault;
    private final AtomiEntityDataImpl data;

    public AtomiGroupImpl(@NotNull AbstractAtomi atomi, @NotNull String name, boolean isDefault, @NotNull AtomiEntityDataImpl data) {
        super(atomi);
        this.name = name;
        this.isDefault = isDefault;
        this.data = data;
        this.data.setUpdateCallback((x) -> update());
    }

    protected void update() {
        atomi.updateGroup(this);
    }

    @Override
    public @NotNull AtomiEntityData data() {
        return data;
    }

    @Override
    public @NotNull String name() {
        return name;
    }

    @Override
    protected @NotNull Tristate defaultPermission(@NotNull String permission) {
        return atomi.defaultPermissionProvider().group(this, permission);
    }

    @Override
    public @NotNull Tristate permission(@NotNull String permission) {
        Tristate value = super.permission(permission);
        if (value != Tristate.UNSET)
            return value;
        return atomi.defaultPermissionProvider().group(this, permission);
    }

    @Override
    public boolean isDefault() {
        return isDefault;
    }

    @Override
    public @NotNull List<AtomiEntity> parents() {
        return List.of();
    }
}
