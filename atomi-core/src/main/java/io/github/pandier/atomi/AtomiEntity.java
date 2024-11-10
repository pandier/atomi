package io.github.pandier.atomi;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Represents any object that holds permission data and additional metadata.
 *
 * <h2>Permissions</h2>
 *
 * <p>Permissions in Atomi are represented as hierarchical strings, with each level seperated by periods (e.g., "minecraft.command.gamemode").
 * Each level in the hiearchy inherits the value of the level above it, unless explicitly set to a different value.
 * Inheritance is therefore implicit; meaning that if an entity has been granted "minecraft.command", then the entity
 * has also been granted "minecraft.command.gamemode", "minecraft.command.teleport" and so on.</p>
 *
 * <p>This system is very inspired by <a href="https://spongepowered.org/">Sponge's</a> permission system.</p>
 *
 * <h2>Inheritance</h2>
 *
 * <p>Any entity can inherit permissions from its parents. This means that if an entity doesn't have
 * a permission set at a lower level, it will inherit the permission from its parents. This behavior
 * is taken into account by most methods in this interface, unless otherwise specified.</p>
 *
 * @see Atomi
 * @see AtomiMetadata
 */
public interface AtomiEntity {

    default boolean hasPermission(@NotNull String permission) {
        return permission(permission) == Tristate.TRUE;
    }

    @NotNull
    Tristate permission(@NotNull String permission);

    @NotNull
    Tristate directPermission(@NotNull String permission);

    void setPermission(@NotNull String permission, @NotNull Tristate value);

    @NotNull
    Map<String, Boolean> directPermissions();

    @NotNull
    AtomiMetadata metadata();

    @NotNull
    AtomiMetadata directMetadata();

    @NotNull
    List<AtomiEntity> parents();
}
