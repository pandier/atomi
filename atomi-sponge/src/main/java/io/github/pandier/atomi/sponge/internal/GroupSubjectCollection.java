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
public class GroupSubjectCollection extends AbstractSubjectCollection {

    public GroupSubjectCollection(AtomiPermissionService service) {
        super(PermissionService.SUBJECTS_GROUP, service);
    }

    @Override
    public Predicate<String> identifierValidityPredicate() {
        return atomi().groupNameValidityPredicate();
    }

    @Override
    public IllegalArgumentException identifierValidityException(String identifier) {
        return new IllegalArgumentException("Group name '" + identifier + "' contains illegal characters");
    }

    @Override
    public Subject loadSubjectInternal(String identifier) {
        return subject(identifier).orElseThrow(() -> new IllegalStateException("Could not find group '" + identifier + "'"));
    }

    @Override
    public Optional<? extends Subject> subject(String identifier) {
        return atomi().group(identifier).map(x -> new GroupSubject(x, this));
    }

    @Override
    public CompletableFuture<Boolean> hasSubject(String identifier) {
        return CompletableFuture.completedFuture(atomi().groupExists(identifier));
    }

    @Override
    public Collection<? extends Subject> loadedSubjects() {
        return atomi().groups().stream().map(x -> new GroupSubject(x, this)).toList();
    }

    @Override
    public CompletableFuture<? extends Set<String>> allIdentifiers() {
        return CompletableFuture.completedFuture(atomi().groupNames());
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
    }
}
