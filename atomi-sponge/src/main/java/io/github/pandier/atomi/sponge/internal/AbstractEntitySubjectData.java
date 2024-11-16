package io.github.pandier.atomi.sponge.internal;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.service.permission.TransferMethod;
import org.spongepowered.api.util.Tristate;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractEntitySubjectData implements SubjectData {

    @Override
    public abstract AbstractEntitySubject subject();

    @Override
    public boolean isTransient() {
        return false;
    }

    @Override
    public Map<Set<Context>, Map<String, Boolean>> allPermissions() {
        return Map.of(SubjectData.GLOBAL_CONTEXT, subject().entity().directPermissions());
    }

    @Override
    public Map<String, Boolean> permissions(Set<Context> contexts) {
        if (!contexts.isEmpty())
            return Map.of();
        return subject().entity().directPermissions();
    }

    @Override
    public CompletableFuture<Boolean> setPermission(Set<Context> contexts, String permission, Tristate value) {
        if (!contexts.isEmpty())
            return CompletableFuture.completedFuture(false);
        subject().entity().setPermission(permission, TristateUtil.atomiTristate(value));
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public CompletableFuture<Boolean> setPermissions(Set<Context> contexts, Map<String, Boolean> permissions, TransferMethod method) {
        if (!contexts.isEmpty())
            return CompletableFuture.completedFuture(false);
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public Tristate fallbackPermissionValue(Set<Context> contexts) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public Map<Set<Context>, Tristate> allFallbackPermissionValues() {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public CompletableFuture<Boolean> setFallbackPermissionValue(Set<Context> contexts, Tristate fallback) {
        if (!contexts.isEmpty())
            return CompletableFuture.completedFuture(false);
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public CompletableFuture<Boolean> clearFallbackPermissionValues() {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public CompletableFuture<Boolean> clearPermissions() {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public CompletableFuture<Boolean> clearPermissions(Set<Context> contexts) {
        if (!contexts.isEmpty())
            return CompletableFuture.completedFuture(false);
        throw new UnsupportedOperationException(); // TODO
    }

    public abstract boolean isChildOf(SubjectReference reference);

    public abstract List<? extends SubjectReference> parents();

    @Override
    public Map<Set<Context>, ? extends List<? extends SubjectReference>> allParents() {
        return Map.of(SubjectData.GLOBAL_CONTEXT, parents());
    }

    @Override
    public List<? extends SubjectReference> parents(Set<Context> contexts) {
        if (!contexts.isEmpty())
            return List.of();
        return parents();
    }

    public abstract CompletableFuture<Boolean> setParents(List<? extends SubjectReference> parents, TransferMethod method);

    @Override
    public CompletableFuture<Boolean> setParents(Set<Context> contexts, List<? extends SubjectReference> parents, TransferMethod method) {
        if (!contexts.isEmpty())
            return CompletableFuture.completedFuture(false);
        return setParents(parents, method);
    }

    public abstract CompletableFuture<Boolean> addParent(SubjectReference parent);

    @Override
    public CompletableFuture<Boolean> addParent(Set<Context> contexts, SubjectReference parent) {
        if (!contexts.isEmpty())
            return CompletableFuture.completedFuture(false);
        return addParent(parent);
    }

    public abstract CompletableFuture<Boolean> removeParent(SubjectReference parent);

    @Override
    public CompletableFuture<Boolean> removeParent(Set<Context> contexts, SubjectReference parent) {
        if (!contexts.isEmpty())
            return CompletableFuture.completedFuture(false);
        return removeParent(parent);
    }

    @Override
    public CompletableFuture<Boolean> clearParents(Set<Context> contexts) {
        if (!contexts.isEmpty())
            return CompletableFuture.completedFuture(false);
        return clearParents();
    }

    @Override
    public Map<Set<Context>, Map<String, String>> allOptions() {
        return Map.of();
    }

    @Override
    public Map<String, String> options(Set<Context> contexts) {
        return Map.of();
    }

    @Override
    public CompletableFuture<Boolean> setOption(Set<Context> contexts, String key, @Nullable String value) {
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public CompletableFuture<Boolean> setOptions(Set<Context> contexts, Map<String, String> options, TransferMethod method) {
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public CompletableFuture<Boolean> clearOptions() {
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public CompletableFuture<Boolean> clearOptions(Set<Context> contexts) {
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public CompletableFuture<Boolean> copyFrom(SubjectData other, TransferMethod method) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public CompletableFuture<Boolean> moveFrom(SubjectData other, TransferMethod method) {
        throw new UnsupportedOperationException(); // TODO
    }
}
