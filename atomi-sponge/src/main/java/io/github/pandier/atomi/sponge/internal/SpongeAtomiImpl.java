package io.github.pandier.atomi.sponge.internal;

import io.github.pandier.atomi.internal.AbstractAtomi;
import io.github.pandier.atomi.internal.option.AtomiOptionRegistry;
import io.github.pandier.atomi.sponge.SpongeAtomi;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Path;

@ApiStatus.Internal
public class SpongeAtomiImpl extends AbstractAtomi implements SpongeAtomi {
    public SpongeAtomiImpl(Path path, Logger logger) {
        super(path, new AtomiOptionRegistry(), logger::error);
    }
}
