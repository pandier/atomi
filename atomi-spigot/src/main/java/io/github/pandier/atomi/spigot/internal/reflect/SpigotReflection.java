package io.github.pandier.atomi.spigot.internal.reflect;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public class SpigotReflection {
    private static final String CRAFT_BUKKIT_PACKAGE;

    static {
        String serverClassName = Bukkit.getServer().getClass().getName();
        CRAFT_BUKKIT_PACKAGE = serverClassName.substring(0, serverClassName.lastIndexOf('.'));
    }

    @NotNull
    public static Class<?> getCraftBukkitClass(String name) throws ClassNotFoundException {
        return Class.forName(CRAFT_BUKKIT_PACKAGE + "." + name);
    }

    @NotNull
    public static Class<?> getMinecraftClass(String name) throws ClassNotFoundException {
        return Class.forName("net.minecraft." + name);
    }
}
