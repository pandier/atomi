package io.github.pandier.atomi.internal;

import io.github.pandier.atomi.AtomiEntity;
import io.github.pandier.atomi.AtomiGroup;
import io.github.pandier.atomi.AtomiMetadata;
import io.github.pandier.atomi.Tristate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

@ApiStatus.Internal
public class AtomiGroupImpl extends AbstractAtomiEntity implements AtomiGroup {
    private final String name;
    private final boolean isDefault;

    public AtomiGroupImpl(@NotNull AbstractAtomi atomi, @NotNull String name, boolean isDefault, @NotNull Map<String, Boolean> permissions, @NotNull AtomiMetadata metadata) {
        super(atomi, permissions, metadata);
        this.name = name;
        this.isDefault = isDefault;
    }

    @Override
    protected void update() {
        atomi.updateGroup(this);
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
