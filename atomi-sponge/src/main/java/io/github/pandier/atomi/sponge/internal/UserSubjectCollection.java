package io.github.pandier.atomi.sponge.internal;

import io.github.pandier.atomi.AtomiUser;
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
    private final Map<String, MemorySubjectData> transientSubjectDatas = new HashMap<>();

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

    protected UserSubject createSubject(AtomiUser user) {
        String identifier = user.uuid().toString();
        MemorySubjectData transientSubjectData = transientSubjectDatas.computeIfAbsent(identifier, x -> new MemorySubjectData(() -> subject(identifier).orElseThrow()));
        return new UserSubject(user, transientSubjectData, this);
    }

    @Override
    public Subject loadSubjectInternal(String identifier) {
        return createSubject(atomi().user(identifierToUuidOrThrow(identifier)));
    }

    @Override
    public Optional<? extends Subject> subject(String identifier) {
        return identifierToUuid(identifier)
                .flatMap(atomi()::userFromCache)
                .map(this::createSubject);
    }

    @Override
    public CompletableFuture<Boolean> hasSubject(String identifier) {
        return CompletableFuture.completedFuture(identifierToUuid(identifier)
                .map(atomi()::userExists)
                .orElse(false));
    }

    @Override
    public Collection<? extends Subject> loadedSubjects() {
        return atomi().cachedUsers().stream().map(this::createSubject).toList();
    }

    @Override
    public CompletableFuture<? extends Set<String>> allIdentifiers() {
        return CompletableFuture.failedFuture(new UnsupportedOperationException("Querying identifiers of all users is not yet supported"));
    }

    @Override
    public CompletableFuture<? extends Map<? extends SubjectReference, Boolean>> allWithPermission(String permission) {
        return CompletableFuture.failedFuture(new UnsupportedOperationException("Querying all users with a permission is not yet supported"));
    }

    @Override
    public CompletableFuture<? extends Map<? extends SubjectReference, Boolean>> allWithPermission(String permission, Cause cause) {
        return CompletableFuture.failedFuture(new UnsupportedOperationException("Querying all users with a permission is not yet supported"));
    }

    @Override
    public Map<? extends Subject, Boolean> loadedWithPermission(String permission) {
        Map<Subject, Boolean> result = new HashMap<>();
        for (AtomiUser user : atomi().cachedUsers()) {
            io.github.pandier.atomi.Tristate value = user.permission(permission);
            if (value == io.github.pandier.atomi.Tristate.UNSET)
                return result;
            result.put(createSubject(user), value.asBoolean());
        }
        return result;
    }

    @Override
    public Map<? extends Subject, Boolean> loadedWithPermission(String permission, Cause cause) {
        return loadedWithPermission(permission);
    }

    @Override
    public void suggestUnload(String identifier) {
        atomi().unloadUser(identifierToUuidOrThrow(identifier));
    }
}
