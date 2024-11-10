package io.github.pandier.atomi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public enum Tristate {
    TRUE(true),
    FALSE(false),
    UNSET(null);

    private final @Nullable Boolean booleanValue;

    Tristate(@Nullable Boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    @NotNull
    public static Tristate of(@Nullable Boolean booleanValue) {
        if (booleanValue == null)
            return UNSET;
        else if (booleanValue)
            return TRUE;
        return FALSE;
    }

    public boolean asBoolean() {
        return booleanValue != null && booleanValue;
    }

    @Nullable
    public Boolean asNullableBoolean() {
        return booleanValue;
    }

    public Optional<Boolean> asOptionalBoolean() {
        return Optional.ofNullable(booleanValue);
    }
}
