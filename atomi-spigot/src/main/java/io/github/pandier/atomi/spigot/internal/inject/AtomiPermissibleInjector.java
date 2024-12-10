package io.github.pandier.atomi.spigot.internal.inject;

import io.github.pandier.atomi.spigot.internal.AtomiPermissible;
import io.github.pandier.atomi.spigot.internal.reflect.SpigotReflection;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.function.Function;
import java.util.logging.Logger;

@ApiStatus.Internal
public class AtomiPermissibleInjector {
    private static final Field PERMISSIBLE_FIELD;

    static {
        try {
            PERMISSIBLE_FIELD = SpigotReflection.getCraftBukkitClass("entity.CraftHumanEntity").getDeclaredField("perm");
            PERMISSIBLE_FIELD.setAccessible(true);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to inject into CraftBukkit", e);
        }
    }

    public static void inject(@NotNull Player player, @NotNull Function<PermissibleBase, AtomiPermissible> permissibleFactory, @NotNull Logger logger) {
        try {
            PermissibleBase oldPermissible = (PermissibleBase) PERMISSIBLE_FIELD.get(player);

            if (!PermissibleBase.class.equals(oldPermissible.getClass())) {
                logger.warning("Player " + player.getName() + " already has a custom permissible (" + oldPermissible.getClass().getName() + ")\n"
                        + "Do you have multiple permission plugins installed? If so, this may cause clashes between those plugins.");
            }

            PERMISSIBLE_FIELD.set(player, permissibleFactory.apply(oldPermissible));
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to inject permissible into player", e);
        }
    }

    public static void uninject(@NotNull Player player) {
        try {
            PermissibleBase permissible = (PermissibleBase) PERMISSIBLE_FIELD.get(player);

            if (permissible instanceof AtomiPermissible atomiPermissible) {
                PERMISSIBLE_FIELD.set(player, atomiPermissible.previousPermissible);
            }
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to uninject permissible from Player", e);
        }
    }
}
