package io.github.pandier.atomi.sponge.internal;

import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.service.permission.TransferMethod;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class NoParentEntitySubjectData extends AbstractEntitySubjectData {
    private final AbstractEntitySubject subject;

    public NoParentEntitySubjectData(AbstractEntitySubject subject) {
        this.subject = subject;
    }

    @Override
    public AbstractEntitySubject subject() {
        return subject;
    }

    @Override
    public boolean isChildOf(SubjectReference reference) {
        return false;
    }

    @Override
    public List<? extends SubjectReference> parents() {
        return List.of();
    }

    @Override
    public CompletableFuture<Boolean> setParents(List<? extends SubjectReference> parents, TransferMethod method) {
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public CompletableFuture<Boolean> addParent(SubjectReference parent) {
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public CompletableFuture<Boolean> removeParent(SubjectReference parent) {
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public CompletableFuture<Boolean> clearParents() {
        return CompletableFuture.completedFuture(true);
    }
}
