package io.github.pandier.atomi.sponge.internal;

import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectReference;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

@ApiStatus.Internal
public class UserSubjectCollection extends AbstractSubjectCollection {

    public UserSubjectCollection(AtomiPermissionService service) {
        super(PermissionService.SUBJECTS_USER, service);
    }

    private static Optional<UUID> identifierToUuid(String identifier) {
        try {
            return Optional.of(UUID.fromString(identifier));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    public UUID identifierToUuidOrThrow(String identifier) {
        return identifierToUuid(identifier).orElseThrow(() -> identifierValidityException(identifier));
    }

    @Override
    public Predicate<String> identifierValidityPredicate() {
        return x -> identifierToUuid(x).isPresent();
    }

    @Override
    public IllegalArgumentException identifierValidityException(String identifier) {
        return new IllegalArgumentException("Provided identifier must be a valid UUID, was " + identifier);
    }

    @Override
    public Subject loadSubjectInternal(String identifier) {
        return new UserSubject(atomi().user(identifierToUuidOrThrow(identifier)), this);
    }

    @Override
    public Optional<? extends Subject> subject(String identifier) {
        return identifierToUuid(identifier)
                .flatMap(atomi()::userFromCache)
                .map(x -> new UserSubject(x, this));
    }

    @Override
    public CompletableFuture<Boolean> hasSubject(String identifier) {
        return CompletableFuture.completedFuture(identifierToUuid(identifier)
                .map(atomi()::userExists)
                .orElse(false));
    }

    @Override
    public Collection<? extends Subject> loadedSubjects() {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public CompletableFuture<? extends Set<String>> allIdentifiers() {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public CompletableFuture<? extends Map<? extends SubjectReference, Boolean>> allWithPermission(String permission) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public CompletableFuture<? extends Map<? extends SubjectReference, Boolean>> allWithPermission(String permission, Cause cause) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public Map<? extends Subject, Boolean> loadedWithPermission(String permission) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public Map<? extends Subject, Boolean> loadedWithPermission(String permission, Cause cause) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void suggestUnload(String identifier) {
        // TODO
    }
}
