package io.github.pandier.atomi.sponge.internal;

import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.service.permission.*;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.plugin.PluginContainer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

@ApiStatus.Internal
public class AtomiPermissionDescription implements PermissionDescription {
    private static final Pattern PERMISSION_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)*(\\.<[a-zA-Z0-9_-]+>)?$");
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\.<[a-zA-Z0-9_-]+>");

    private final AtomiPermissionService service;
    private final String id;
    // Represents the id without the placeholder
    private final String strippedId;
    private final Component description;
    private final PluginContainer owner;

    public AtomiPermissionDescription(AtomiPermissionService service, String id, String strippedId, Component description, PluginContainer owner) {
        this.service = service;
        this.id = id;
        this.strippedId = strippedId;
        this.description = description;
        this.owner = owner;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public Optional<Component> description() {
        return Optional.ofNullable(description);
    }

    @Override
    public Optional<PluginContainer> owner() {
        return Optional.of(owner);
    }

    @Override
    public Tristate defaultValue() {
        return service.defaults().permissionValue(strippedId);
    }

    @Override
    public boolean query(Subject subj) {
        return subj.hasPermission(strippedId);
    }

    @Override
    public boolean query(Subject subj, ResourceKey key) {
        return subj.hasPermission(strippedId + "." + key.namespace() + "." + key.value());
    }

    @Override
    public boolean query(Subject subj, String... parameters) {
        if (parameters.length == 0)
            return query(subj);
        if (parameters.length == 1)
            return query(subj, parameters[0]);
        StringBuilder builder = new StringBuilder(strippedId);
        for (String parameter : parameters)
            builder.append(".").append(parameter);
        return subj.hasPermission(builder.toString());
    }

    @Override
    public boolean query(Subject subj, String parameter) {
        Objects.requireNonNull(parameter, "parameter");
        return subj.hasPermission(strippedId + "." + parameter);
    }

    @Override
    public Map<? extends Subject, Boolean> assignedSubjects(String collectionIdentifier) {
        SubjectCollection collection = service.collection(collectionIdentifier).orElse(null);
        if (collection == null) return Map.of();
        return collection.loadedWithPermission(strippedId);
    }

    @Override
    public CompletableFuture<? extends Map<? extends SubjectReference, Boolean>> findAssignedSubjects(String collectionIdentifier) {
        return service.loadCollection(collectionIdentifier)
                .thenCompose(collection -> collection.allWithPermission(strippedId));
    }

    public static class Builder implements PermissionDescription.Builder {
        private final AtomiPermissionService service;
        private final PluginContainer owner;
        private String id = null;
        private Component description = null;
        private Tristate defaultValue = Tristate.UNDEFINED;
        private final Map<String, Boolean> roles = new HashMap<>();

        public Builder(AtomiPermissionService service, PluginContainer owner) {
            this.service = service;
            this.owner = owner;
        }

        @Override
        public PermissionDescription.Builder id(String id) {
            Objects.requireNonNull(id, "id");
            if (!PERMISSION_PATTERN.matcher(id).matches())
                throw new IllegalArgumentException("Permission '" + id + "' does not match the allowed format '" + PERMISSION_PATTERN.pattern() + "'");
            this.id = id;
            return this;
        }

        @Override
        public PermissionDescription.Builder description(@Nullable Component description) {
            this.description = description;
            return this;
        }

        @Override
        public PermissionDescription.Builder assign(String role, boolean value) {
            Objects.requireNonNull(role, "role");
            this.roles.put(role, value);
            return this;
        }

        @Override
        public PermissionDescription.Builder defaultValue(Tristate defaultValue) {
            this.defaultValue = Objects.requireNonNull(defaultValue, "defaultValue");
            return this;
        }

        @Override
        public PermissionDescription register() throws IllegalStateException {
            if (id == null)
                throw new IllegalStateException("No id set");

            String strippedId = PLACEHOLDER_PATTERN.matcher(id).replaceAll("");

            AtomiPermissionDescription permissionDescription = new AtomiPermissionDescription(service, id, strippedId, description, owner);
            service.addDescription(permissionDescription);

            // Set the default value
            if (defaultValue != Tristate.UNDEFINED)
                service.defaults().transientSubjectData().setPermission(SubjectData.GLOBAL_CONTEXT, strippedId, defaultValue);

            for (Map.Entry<String, Boolean> entry : roles.entrySet()) {
                Subject subject = service.roleTemplateCollection().get(entry.getKey());
                subject.transientSubjectData().setPermission(SubjectData.GLOBAL_CONTEXT, strippedId, Tristate.fromBoolean(entry.getValue()));
            }

            return permissionDescription;
        }
    }
}
