package io.github.pandier.atomi;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public final class AtomiOptions {
    public static final AtomiOption<Component> PREFIX = AtomiOption.of("prefix", AtomiOptionType.COMPONENT);
    public static final AtomiOption<Component> SUFFIX = AtomiOption.of("suffix", AtomiOptionType.COMPONENT);
    public static final AtomiOption<NamedTextColor> COLOR = AtomiOption.of("color", AtomiOptionType.NAMED_TEXT_COLOR);
}
