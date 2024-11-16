package io.github.pandier.atomi.sponge.internal;

import io.github.pandier.atomi.Atomi;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectReference;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@ApiStatus.Internal
public abstract class AbstractSubjectCollection implements SubjectCollection {
    private final String identifier;
    protected final AtomiPermissionService service;

    protected AbstractSubjectCollection(String identifier, AtomiPermissionService service) {
        this.identifier = identifier;
        this.service = service;
    }

    @Override
    public String identifier() {
        return identifier;
    }

    public abstract IllegalArgumentException identifierValidityException(String identifier);

    public abstract Subject loadSubjectInternal(String identifier);

    @Override
    public CompletableFuture<? extends Subject> loadSubject(String identifier) {
        if (!identifierValidityPredicate().test(identifier))
            throw identifierValidityException(identifier);
        try {
            return CompletableFuture.completedFuture(loadSubjectInternal(identifier));
        } catch (Exception ex) {
            return CompletableFuture.failedFuture(ex);
        }
    }

    @Override
    public CompletableFuture<? extends Map<String, ? extends Subject>> loadSubjects(Iterable<String> identifiers) {
        Map<String, Subject> subjects = new HashMap<>();
        for (String identifier : identifiers) {
            if (!identifierValidityPredicate().test(identifier)) continue;
            try {
                subjects.put(identifier, loadSubjectInternal(identifier));
            } catch (Exception ex) {
                return CompletableFuture.failedFuture(ex);
            }
        }
        return CompletableFuture.completedFuture(subjects);
    }

    @Override
    public SubjectReference newSubjectReference(String subjectIdentifier) {
        return service().newSubjectReference(identifier(), subjectIdentifier);
    }

    @Override
    public Subject defaults() {
        return service().defaults(identifier());
    }

    public AtomiPermissionService service() {
        return service;
    }

    public Atomi atomi() {
        return service().atomi();
    }
}
