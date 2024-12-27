package io.github.pandier.atomi.sponge.internal;

import io.github.pandier.atomi.AtomiGroup;
import io.github.pandier.atomi.AtomiUser;
import io.github.pandier.atomi.internal.AbstractAtomi;
import io.github.pandier.atomi.internal.option.AtomiOptionRegistry;
import io.github.pandier.atomi.sponge.SpongeAtomi;
import io.github.pandier.atomi.sponge.internal.event.AtomiGroupUpdateEventImpl;
import io.github.pandier.atomi.sponge.internal.event.AtomiUserUpdateEventImpl;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.EventContext;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.plugin.PluginContainer;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ApiStatus.Internal
public class SpongeAtomiImpl extends AbstractAtomi implements SpongeAtomi {
    protected final PluginContainer pluginContainer;

    protected final KeyValueLock<String, Boolean> groupUpdateLock = new KeyValueLock<>();
    protected final KeyValueLock<UUID, Boolean> userUpdateLock = new KeyValueLock<>();

    public SpongeAtomiImpl(PluginContainer pluginContainer, AtomiOptionRegistry optionRegistry, Path path, Logger logger) {
        super(optionRegistry, path, logger::error);
        this.pluginContainer = pluginContainer;
    }

    @Override
    public void updateGroup(@NotNull AtomiGroup group, boolean save) {
        if (!groupUpdateLock.lock(group.name(), save, save))
            return;
        Sponge.eventManager().post(new AtomiGroupUpdateEventImpl(group, createEventCause()));
        save = groupUpdateLock.unlock(group.name());

        super.updateGroup(group, save);
    }

    @Override
    public void updateUser(@NotNull AtomiUser user, boolean save) {
        if (!userUpdateLock.lock(user.uuid(), save, save))
            return;
        Sponge.eventManager().post(new AtomiUserUpdateEventImpl(user, Sponge.server(), createEventCause()));
        save = userUpdateLock.unlock(user.uuid());

        super.updateUser(user, save);
    }

    protected Cause createEventCause() {
        EventContext context = EventContext.builder().add(EventContextKeys.PLUGIN, pluginContainer).build();
        return Cause.builder().append(pluginContainer).build(context);
    }

    protected static class KeyValueLock<T, V> {
        private final Map<T, V> map = new HashMap<>();

        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
        public synchronized boolean lock(T key, V value, boolean replace) {
            V currentValue = map.get(key);
            if (currentValue == null) {
                map.put(key, value);
                return true;
            }
            if (replace)
                map.put(key, value);
            return false;
        }

        public synchronized V unlock(T key) {
            return map.remove(key);
        }
    }
}
