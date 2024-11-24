package io.github.pandier.atomi.sponge.internal;

import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.service.permission.TransferMethod;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@ApiStatus.Internal
public class UserSubjectData extends AbstractEntitySubjectData {
    private final UserSubject subject;

    public UserSubjectData(UserSubject subject) {
        this.subject = subject;
    }

    @Override
    public UserSubject subject() {
        return subject;
    }

    @Override
    public boolean isChildOf(SubjectReference reference) {
        if (!reference.collectionIdentifier().equals(PermissionService.SUBJECTS_GROUP))
            return false;
        return subject().entity().group().name().equals(reference.subjectIdentifier());
    }

    @Override
    public List<? extends SubjectReference> parents() {
        return List.of(subject.service().newSubjectReference(PermissionService.SUBJECTS_GROUP, subject().entity().group().name()));
    }

    @Override
    public CompletableFuture<Boolean> setParents(List<? extends SubjectReference> parents, TransferMethod method) {
        if (parents.isEmpty()) {
            if (method == TransferMethod.OVERWRITE)
                return clearParents();
            return CompletableFuture.completedFuture(true);
        }
        return addParent(parents.getLast());
    }

    @Override
    public CompletableFuture<Boolean> addParent(SubjectReference parent) {
        if (!parent.collectionIdentifier().equals(PermissionService.SUBJECTS_GROUP))
            return CompletableFuture.completedFuture(false);
        boolean success = subject().entity().setGroupByName(parent.subjectIdentifier());
        return CompletableFuture.completedFuture(success);
    }

    @Override
    public CompletableFuture<Boolean> removeParent(SubjectReference parent) {
        if (!isChildOf(parent))
            return CompletableFuture.completedFuture(false);
        return clearParents();
    }

    @Override
    public CompletableFuture<Boolean> clearParents() {
        if (subject().entity().group().isDefault())
            return CompletableFuture.completedFuture(false);
        subject().entity().setGroup(null);
        return CompletableFuture.completedFuture(true);
    }
}
