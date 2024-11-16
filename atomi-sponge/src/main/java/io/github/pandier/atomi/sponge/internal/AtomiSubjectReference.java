package io.github.pandier.atomi.sponge.internal;

import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectReference;

import java.util.concurrent.CompletableFuture;

@ApiStatus.Internal
public class AtomiSubjectReference implements SubjectReference {
    private final AtomiPermissionService service;
    private final String collectionIdentifier;
    private final String subjectIdentifier;

    public AtomiSubjectReference(AtomiPermissionService service, String collectionIdentifier, String subjectIdentifier) {
        this.service = service;
        this.collectionIdentifier = collectionIdentifier;
        this.subjectIdentifier = subjectIdentifier;
    }

    @Override
    public String collectionIdentifier() {
        return collectionIdentifier;
    }

    @Override
    public String subjectIdentifier() {
        return subjectIdentifier;
    }

    @Override
    public CompletableFuture<? extends Subject> resolve() {
        return service.loadCollection(collectionIdentifier)
                .thenCompose(collection -> collection.loadSubject(subjectIdentifier));
    }
}
