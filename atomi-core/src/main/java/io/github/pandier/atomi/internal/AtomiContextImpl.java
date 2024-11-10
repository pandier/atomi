package io.github.pandier.atomi.internal;

import io.github.pandier.atomi.AtomiContext;
import io.github.pandier.atomi.AtomiEntity;
import io.github.pandier.atomi.AtomiMetadata;
import io.github.pandier.atomi.Tristate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApiStatus.Internal
public class AtomiContextImpl implements AtomiContext {
    private final String identifier;
    private final HashMap<String, Boolean> permissions;
    private final AtomiMetadata metadata;

    private AtomiContextImpl(String identifier, HashMap<String, Boolean> permissions, AtomiMetadata metadata) {
        this.identifier = identifier;
        this.permissions = permissions;
        this.metadata = metadata;
    }

    @Override
    public @NotNull String identifier() {
        return identifier;
    }

    @Override
    public @NotNull Tristate permission(@NotNull String permission) {
        return Tristate.of(permissions.get(permission));
    }

    @Override
    public @NotNull Tristate directPermission(@NotNull String permission) {
        return Tristate.of(permissions.get(permission));
    }

    @Override
    public void setPermission(@NotNull String permission, @NotNull Tristate value) {
        Boolean booleanValue = value.asNullableBoolean();
        if (booleanValue == null) {
            permissions.remove(permission);
        } else {
            permissions.put(permission, booleanValue);
        }
    }

    @Override
    public @NotNull AtomiMetadata directMetadata() {
        return new AtomiMetadataImpl(metadata);
    }

    @Override
    public @NotNull Map<String, Boolean> directPermissions() {
        return new HashMap<>(permissions);
    }

    @Override
    public @NotNull AtomiMetadata metadata() {
        return metadata;
    }

    @Override
    public @NotNull List<AtomiEntity> parents() {
        return List.of();
    }

    @ApiStatus.Internal
    public static class Builder implements AtomiContext.Builder {
        private String identifier;
        private HashMap<String, Boolean> permissions = new HashMap<>();
        private AtomiMetadata metadata = null;

        public Builder(@NotNull String identifier) {
            this.identifier = identifier;
        }

        public Builder setIdentifier(@NotNull String identifier) {
            this.identifier = identifier;
            return this;
        }

        public Builder setPermission(@NotNull String permission, Tristate value) {
            if (permissions == null) {
                permissions = new HashMap<>();
            }
            permissions.put(permission, value.asNullableBoolean());
            return this;
        }

        public Builder setMetadata(@NotNull AtomiMetadata metadata) {
            this.metadata = metadata;
            return this;
        }

        @Override
        public AtomiContext build() {
            return new AtomiContextImpl(identifier, permissions, metadata != null ? new AtomiMetadataImpl(metadata) : new AtomiMetadataImpl());
        }
    }
}
