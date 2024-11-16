package io.github.pandier.atomi.internal;

import io.github.pandier.atomi.AtomiEntity;
import io.github.pandier.atomi.AtomiMetadata;
import io.github.pandier.atomi.Tristate;
import io.github.pandier.atomi.internal.permission.PermissionTree;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@ApiStatus.Internal
public abstract class AbstractAtomiEntity implements AtomiEntity {
    protected final AbstractAtomi atomi;
    protected final PermissionTree permissionTree;
    protected final AtomiLinkedMetadataImpl metadata;

    protected AbstractAtomiEntity(AbstractAtomi atomi) {
        this(atomi, Map.of(), AtomiMetadata.create());
    }

    protected AbstractAtomiEntity(AbstractAtomi atomi, Map<String, Boolean> permissions, AtomiMetadata metadata) {
        this.atomi = atomi;
        this.permissionTree = new PermissionTree(permissions);
        this.metadata = new AtomiLinkedMetadataImpl(metadata, () -> parents().stream().map(AtomiEntity::metadata), (ignored) -> update());
    }

    protected void update() {
    }

    @Override
    public @NotNull Tristate permission(@NotNull String permission) {
        Tristate value = directPermission(permission);
        if (value != Tristate.UNSET)
            return value;
        for (AtomiEntity parent : parents()) {
            Tristate parentValue = parent.permission(permission);
            if (parentValue != Tristate.UNSET)
                return parentValue;
        }
        return defaultPermission(permission);
    }

    protected @NotNull Tristate defaultPermission(@NotNull String permission) {
        return Tristate.UNSET;
    }

    @Override
    public @NotNull Tristate directPermission(@NotNull String permission) {
        if (!atomi.permissionValidityPredicate().test(permission))
            return Tristate.UNSET;
        return permissionTree.get(permission);
    }

    @Override
    public void setPermission(@NotNull String permission, @NotNull Tristate value) {
        if (!atomi.permissionValidityPredicate().test(permission))
            throw new IllegalArgumentException("Permission '" + permission + "' contains illegal characters");
        permissionTree.set(permission, value);
        update();
    }

    @Override
    public @NotNull Map<String, Boolean> directPermissions() {
        return permissionTree.asMap();
    }

    @Override
    public @NotNull AtomiMetadata metadata() {
        return metadata;
    }

    @Override
    public @NotNull AtomiMetadata directMetadata() {
        return metadata.direct();
    }
}
