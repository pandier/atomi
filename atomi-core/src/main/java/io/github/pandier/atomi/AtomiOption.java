package io.github.pandier.atomi;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.regex.Pattern;

public final class AtomiOption<T> {
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_\\-.:]+$");

    private final String name;
    private final AtomiOptionType<T> type;

    private AtomiOption(String name, AtomiOptionType<T> type) {
        this.name = name;
        this.type = type;
    }

    @NotNull
    public static <T> AtomiOption<T> of(@NotNull String name, @NotNull AtomiOptionType<T> type) {
        if (!NAME_PATTERN.matcher(name).matches())
            throw new IllegalArgumentException("Option name '" + name + "' does not match the allowed format '" + NAME_PATTERN.pattern() + "'");
        return new AtomiOption<>(name, type);
    }

    @NotNull
    public String name() {
        return name;
    }

    @NotNull
    public AtomiOptionType<T> type() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AtomiOption<?> that = (AtomiOption<?>) o;
        return Objects.equals(name, that.name) && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }

    @Override
    public String toString() {
        return "AtomiOption{" +
                "name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}
