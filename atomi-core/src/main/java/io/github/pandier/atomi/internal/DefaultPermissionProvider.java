package io.github.pandier.atomi.internal;

import io.github.pandier.atomi.AtomiGroup;
import io.github.pandier.atomi.AtomiUser;
import io.github.pandier.atomi.Tristate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public interface DefaultPermissionProvider {
    DefaultPermissionProvider UNSET = new DefaultPermissionProvider() {};

    default Tristate user(@NotNull AtomiUser user, @NotNull String permission) {
        return Tristate.UNSET;
    }

    default Tristate group(@NotNull AtomiGroup group, @NotNull String permission) {
        return Tristate.UNSET;
    }
}
