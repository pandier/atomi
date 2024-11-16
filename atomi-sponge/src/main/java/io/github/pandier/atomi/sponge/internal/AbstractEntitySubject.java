package io.github.pandier.atomi.sponge.internal;

import io.github.pandier.atomi.Atomi;
import io.github.pandier.atomi.AtomiEntity;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.*;
import org.spongepowered.api.util.Tristate;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@ApiStatus.Internal
public abstract class AbstractEntitySubject implements Subject {
    private final String identifier;
    private final AbstractSubjectCollection collection;

    public AbstractEntitySubject(String identifier, AbstractSubjectCollection collection) {
        this.identifier = identifier;
        this.collection = collection;
    }

    public abstract AtomiEntity entity();

    @Override
    public abstract AbstractEntitySubjectData subjectData();

    @Override
    public SubjectCollection containingCollection() {
        return collection;
    }

    @Override
    public SubjectReference asSubjectReference() {
        return collection.newSubjectReference(identifier());
    }

    @Override
    public Optional<?> associatedObject() {
        return Optional.empty();
    }

    @Override
    public boolean isSubjectDataPersisted() {
        return true;
    }

    @Override
    public SubjectData transientSubjectData() {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public Tristate permissionValue(String permission, Cause cause) {
        return permissionValue(permission, SubjectData.GLOBAL_CONTEXT);
    }

    @Override
    public Tristate permissionValue(String permission, Set<Context> contexts) {
        // TODO include transient subject data?
        Tristate tristate = TristateUtil.spongeTristate(entity().permission(permission));
        if (tristate == Tristate.UNDEFINED)
            tristate = collection.defaults().permissionValue(permission);
        if (tristate == Tristate.UNDEFINED)
            tristate = service().defaults().permissionValue(permission);
        return tristate;
    }

    @Override
    public boolean isChildOf(SubjectReference parent, Cause cause) {
        return subjectData().isChildOf(parent);
    }

    @Override
    public boolean isChildOf(SubjectReference parent, Set<Context> contexts) {
        return isChildOf(parent, (Cause) null);
    }

    @Override
    public List<? extends SubjectReference> parents() {
        // TODO include transient subject data?
        return subjectData().parents(SubjectData.GLOBAL_CONTEXT);
    }

    @Override
    public List<? extends SubjectReference> parents(Cause cause) {
        return parents();
    }

    @Override
    public List<? extends SubjectReference> parents(Set<Context> contexts) {
        return parents();
    }

    @Override
    public Optional<String> option(String key, Cause cause) {
        // TODO include transient subject data?
        return Optional.empty();
    }

    @Override
    public Optional<String> option(String key, Set<Context> contexts) {
        return option(key, (Cause) null);
    }

    @Override
    public String identifier() {
        return identifier;
    }

    public AtomiPermissionService service() {
        return collection.service();
    }

    public Atomi atomi() {
        return collection.atomi();
    }
}