package io.github.pandier.atomi.sponge.internal;

import io.github.pandier.atomi.internal.permission.PermissionTree;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.service.permission.TransferMethod;
import org.spongepowered.api.util.Tristate;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class MemorySubjectData implements SubjectData {
    private final PermissionTree permissions = new PermissionTree();
    private final Supplier<Subject> subjectSuppplier;

    public MemorySubjectData(Supplier<Subject> subjectSupplier) {
        this.subjectSuppplier = subjectSupplier;
    }

    @Override
    public Subject subject() {
        return subjectSuppplier.get();
    }

    @Override
    public boolean isTransient() {
        return true;
    }

    public Tristate permissionValue(String permission) {
        return TristateUtil.spongeTristate(this.permissions.get(permission));
    }

    @Override
    public Map<Set<Context>, Map<String, Boolean>> allPermissions() {
        return Map.of(Set.of(), this.permissions.asMap());
    }

    @Override
    public Map<String, Boolean> permissions(Set<Context> contexts) {
        if (!contexts.isEmpty())
            return Map.of();
        return this.permissions.asMap();
    }

    @Override
    public CompletableFuture<Boolean> setPermission(Set<Context> contexts, String permission, Tristate value) {
        if (!contexts.isEmpty())
            return CompletableFuture.completedFuture(false);
        this.permissions.set(permission, TristateUtil.atomiTristate(value));
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public CompletableFuture<Boolean> setPermissions(Set<Context> contexts, Map<String, Boolean> permissions, TransferMethod method) {
        if (!contexts.isEmpty())
            return CompletableFuture.completedFuture(false);
        if (method == TransferMethod.OVERWRITE)
            this.permissions.clear();
        this.permissions.setAll(permissions);
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public Tristate fallbackPermissionValue(Set<Context> contexts) {
        if (!contexts.isEmpty())
            return Tristate.UNDEFINED;
        return TristateUtil.spongeTristate(this.permissions.getRoot());
    }

    @Override
    public Map<Set<Context>, Tristate> allFallbackPermissionValues() {
        return Map.of(Set.of(), TristateUtil.spongeTristate(this.permissions.getRoot()));
    }

    @Override
    public CompletableFuture<Boolean> setFallbackPermissionValue(Set<Context> contexts, Tristate fallback) {
        if (!contexts.isEmpty())
            return CompletableFuture.completedFuture(false);
        this.permissions.setRoot(TristateUtil.atomiTristate(fallback));
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public CompletableFuture<Boolean> clearFallbackPermissionValues() {
        this.permissions.setRoot(io.github.pandier.atomi.Tristate.UNSET);
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public CompletableFuture<Boolean> clearPermissions() {
        this.permissions.clear();
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public CompletableFuture<Boolean> clearPermissions(Set<Context> contexts) {
        if (!contexts.isEmpty())
            return CompletableFuture.completedFuture(false);
        this.permissions.clear();
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public Map<Set<Context>, ? extends List<? extends SubjectReference>> allParents() {
        return Map.of();
    }

    @Override
    public List<? extends SubjectReference> parents(Set<Context> contexts) {
        return List.of();
    }

    @Override
    public CompletableFuture<Boolean> setParents(Set<Context> contexts, List<? extends SubjectReference> parents, TransferMethod method) {
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public CompletableFuture<Boolean> addParent(Set<Context> contexts, SubjectReference parent) {
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public CompletableFuture<Boolean> removeParent(Set<Context> contexts, SubjectReference parent) {
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public CompletableFuture<Boolean> clearParents() {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public CompletableFuture<Boolean> clearParents(Set<Context> contexts) {
        return CompletableFuture.completedFuture(true);
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
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public CompletableFuture<Boolean> clearOptions(Set<Context> contexts) {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public CompletableFuture<Boolean> copyFrom(SubjectData other, TransferMethod method) {
        if (method == TransferMethod.OVERWRITE)
            this.permissions.clear();
        this.permissions.setAll(other.permissions(SubjectData.GLOBAL_CONTEXT));
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public CompletableFuture<Boolean> moveFrom(SubjectData other, TransferMethod method) {
        if (method == TransferMethod.OVERWRITE)
            this.permissions.clear();
        this.permissions.setAll(other.permissions(SubjectData.GLOBAL_CONTEXT));
        other.clearPermissions(SubjectData.GLOBAL_CONTEXT);
        return CompletableFuture.completedFuture(true);
    }
}
