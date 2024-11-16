package io.github.pandier.atomi.sponge.internal;

import io.github.pandier.atomi.Tristate;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class TristateUtil {
    private TristateUtil() {
    }

    public static Tristate atomiTristate(org.spongepowered.api.util.Tristate tristate) {
        return Tristate.of(tristate.asNullableBoolean());
    }

    public static org.spongepowered.api.util.Tristate spongeTristate(Tristate tristate) {
        return org.spongepowered.api.util.Tristate.fromNullableBoolean(tristate.asNullableBoolean());
    }
}
