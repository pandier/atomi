package io.github.pandier.atomi.sponge.internal;

import com.google.inject.Inject;
import io.github.pandier.atomi.sponge.SpongeAtomi;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.ProvideServiceEvent;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import java.nio.file.Path;

@Plugin("atomi")
@ApiStatus.Internal
public class SpongeAtomiPlugin {
    public static SpongeAtomi atomi;

    @Inject
    private Logger logger;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configPath;

    public SpongeAtomiPlugin() {
        atomi = new SpongeAtomiImpl(configPath, logger);
    }

    @Listener
    private void providePermissionService(ProvideServiceEvent.EngineScoped<PermissionService> event) {
        if (atomi != null) {
            event.suggest(() -> new AtomiPermissionService(atomi));
        } else {
            logger.error("Could not provide permission service, because Atomi was not loaded yet");
        }
    }
}
