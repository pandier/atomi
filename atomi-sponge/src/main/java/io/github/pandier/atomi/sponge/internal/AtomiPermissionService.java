package io.github.pandier.atomi.sponge.internal;

import io.github.pandier.atomi.Atomi;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.api.service.permission.*;
import org.spongepowered.plugin.PluginContainer;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

@ApiStatus.Internal
public class AtomiPermissionService implements PermissionService {
    private final UserSubjectCollection userSubjectCollection = new UserSubjectCollection(this);
    private final GroupSubjectCollection groupSubjectCollection = new GroupSubjectCollection(this);
    private final FactorySubjectCollection defaultsCollection = new FactorySubjectCollection(PermissionService.SUBJECTS_DEFAULT, this);
    private final FactorySubjectCollection roleTemplateCollection = new FactorySubjectCollection(PermissionService.SUBJECTS_ROLE_TEMPLATE, this);
    private final Map<String, SubjectCollection> collectionMap = new HashMap<>();
    private final Map<String, PermissionDescription> permissionDescriptions = new ConcurrentHashMap<>();

    private final Atomi atomi;

    public AtomiPermissionService(Atomi atomi) {
        this.atomi = atomi;

        this.collectionMap.put(userSubjectCollection.identifier(), userSubjectCollection);
        this.collectionMap.put(groupSubjectCollection.identifier(), groupSubjectCollection);
        this.collectionMap.put(defaultsCollection.identifier(), defaultsCollection);
        this.collectionMap.put(roleTemplateCollection.identifier(), roleTemplateCollection);
        // TODO: Different default permission values for system and command blocks
        this.collectionMap.put(PermissionService.SUBJECTS_SYSTEM, new FactorySubjectCollection(PermissionService.SUBJECTS_SYSTEM, this));
        this.collectionMap.put(PermissionService.SUBJECTS_COMMAND_BLOCK, new FactorySubjectCollection(PermissionService.SUBJECTS_COMMAND_BLOCK, this));
    }

    @Override
    public SubjectCollection userSubjects() {
        return this.userSubjectCollection;
    }

    @Override
    public SubjectCollection groupSubjects() {
        return this.groupSubjectCollection;
    }

    public FactorySubjectCollection roleTemplateCollection() {
        return this.roleTemplateCollection;
    }

    public Subject defaults(String collectionIdentifier) {
        return this.defaultsCollection.get(collectionIdentifier);
    }

    @Override
    public Subject defaults() {
        return defaults("default");
    }

    @Override
    public Predicate<String> identifierValidityPredicate() {
        return x -> true;
    }

    @Override
    public CompletableFuture<? extends SubjectCollection> loadCollection(String identifier) {
        Optional<? extends SubjectCollection> collection = collection(identifier);
        if (collection.isEmpty())
            return CompletableFuture.failedFuture(new IllegalStateException("Could not find collection '" + identifier + "'"));
        return CompletableFuture.completedFuture(collection.get());
    }

    @Override
    public Optional<? extends SubjectCollection> collection(String identifier) {
        return Optional.ofNullable(collectionMap.get(identifier));
    }

    @Override
    public CompletableFuture<Boolean> hasCollection(String identifier) {
        return CompletableFuture.completedFuture(collectionMap.containsKey(identifier));
    }

    @Override
    public Map<String, ? extends SubjectCollection> loadedCollections() {
        return Map.copyOf(collectionMap);
    }

    @Override
    public CompletableFuture<? extends Set<String>> allIdentifiers() {
        return CompletableFuture.completedFuture(Set.copyOf(collectionMap.keySet()));
    }

    @Override
    public SubjectReference newSubjectReference(String collectionIdentifier, String subjectIdentifier) {
        Objects.requireNonNull(collectionIdentifier, "collectionIdentifier");
        Objects.requireNonNull(subjectIdentifier, "subjectIdentifier");
        return new AtomiSubjectReference(this, collectionIdentifier, subjectIdentifier);
    }

    @Override
    public PermissionDescription.Builder newDescriptionBuilder(PluginContainer plugin) {
        Objects.requireNonNull(plugin, "plugin");
        return new AtomiPermissionDescription.Builder(this, plugin);
    }

    @Override
    public Optional<? extends PermissionDescription> description(String permission) {
        Objects.requireNonNull(permission, "permission");
        return Optional.ofNullable(permissionDescriptions.get(permission.toLowerCase(Locale.ROOT)));
    }

    @Override
    public Collection<? extends PermissionDescription> descriptions() {
        return List.copyOf(permissionDescriptions.values());
    }

    void addDescription(AtomiPermissionDescription permissionDescription) {
        permissionDescriptions.put(permissionDescription.id().toLowerCase(Locale.ROOT), permissionDescription);
    }

    public Atomi atomi() {
        return atomi;
    }
}
