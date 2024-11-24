package io.github.pandier.atomi.sponge.internal;

import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.*;
import org.spongepowered.api.util.Tristate;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

@ApiStatus.Internal
public class FactorySubjectCollection implements SubjectCollection {
    private final String identifier;
    private final AtomiPermissionService service;
    private final Map<String, Subject> subjects = new HashMap<>();

    public FactorySubjectCollection(String identifier, AtomiPermissionService service) {
        this.identifier = identifier;
        this.service = service;
    }

    @Override
    public String identifier() {
        return identifier;
    }

    @Override
    public Predicate<String> identifierValidityPredicate() {
        return x -> true;
    }

    public synchronized Subject get(String identifier) {
        return subjects.computeIfAbsent(identifier, FactorySubject::new);
    }

    @Override
    public CompletableFuture<? extends Subject> loadSubject(String identifier) {
        return CompletableFuture.completedFuture(get(identifier));
    }

    @Override
    public Optional<? extends Subject> subject(String identifier) {
        return Optional.of(get(identifier));
    }

    @Override
    public synchronized CompletableFuture<Boolean> hasSubject(String identifier) {
        return CompletableFuture.completedFuture(subjects.containsKey(identifier));
    }

    @Override
    public CompletableFuture<? extends Map<String, ? extends Subject>> loadSubjects(Iterable<String> identifiers) {
        Map<String, Subject> result = new HashMap<>();
        for (String identifier : identifiers)
            result.put(identifier, get(identifier));
        return CompletableFuture.completedFuture(result);
    }

    @Override
    public synchronized Collection<? extends Subject> loadedSubjects() {
        return List.copyOf(subjects.values());
    }

    @Override
    public synchronized CompletableFuture<? extends Set<String>> allIdentifiers() {
        return CompletableFuture.completedFuture(Set.copyOf(subjects.keySet()));
    }

    @Override
    public SubjectReference newSubjectReference(String subjectIdentifier) {
        return service.newSubjectReference(identifier(), subjectIdentifier);
    }

    @Override
    public CompletableFuture<? extends Map<? extends SubjectReference, Boolean>> allWithPermission(String permission) {
        return CompletableFuture.completedFuture(findWithPermission(permission, Subject::permissionValue, Subject::asSubjectReference));
    }

    @Override
    public CompletableFuture<? extends Map<? extends SubjectReference, Boolean>> allWithPermission(String permission, Cause cause) {
        return CompletableFuture.completedFuture(findWithPermission(permission, (s, p) -> s.permissionValue(p, cause), Subject::asSubjectReference));
    }

    @Override
    public Map<? extends Subject, Boolean> loadedWithPermission(String permission) {
        return findWithPermission(permission, Subject::permissionValue, x -> x);
    }

    @Override
    public Map<? extends Subject, Boolean> loadedWithPermission(String permission, Cause cause) {
        return findWithPermission(permission, (s, p) -> s.permissionValue(p, cause), x -> x);
    }

    private <S> Map<S, Boolean> findWithPermission(String permission, BiFunction<Subject, String, Tristate> permissionFunction, Function<Subject, S> subjectMapper) {
        Map<S, Boolean> result = new HashMap<>();
        for (Subject subject : loadedSubjects()) {
            Tristate value = permissionFunction.apply(subject, permission);
            if (value != Tristate.UNDEFINED)
                result.put(subjectMapper.apply(subject), value.asBoolean());
        }
        return result;
    }

    @Override
    public Subject defaults() {
        return service.defaults(identifier());
    }

    @Override
    public void suggestUnload(String identifier) {
    }

    public class FactorySubject implements Subject {
        private final String identifier;
        private final MemorySubjectData data = new MemorySubjectData(this);

        public FactorySubject(String identifier) {
            this.identifier = identifier;
        }

        @Override
        public SubjectCollection containingCollection() {
            return FactorySubjectCollection.this;
        }

        @Override
        public SubjectReference asSubjectReference() {
            return newSubjectReference(this.identifier());
        }

        @Override
        public Optional<?> associatedObject() {
            return Optional.empty();
        }

        @Override
        public boolean isSubjectDataPersisted() {
            return false;
        }

        @Override
        public SubjectData subjectData() {
            return this.transientSubjectData();
        }

        @Override
        public SubjectData transientSubjectData() {
            return this.data;
        }

        @Override
        public Tristate permissionValue(String permission, Cause cause) {
            return this.data.permissionValue(permission);
        }

        @Override
        public Tristate permissionValue(String permission, Set<Context> contexts) {
            return permissionValue(permission, (Cause) null);
        }

        @Override
        public boolean isChildOf(SubjectReference parent, Cause cause) {
            return false;
        }

        @Override
        public boolean isChildOf(SubjectReference parent, Set<Context> contexts) {
            return false;
        }

        @Override
        public List<? extends SubjectReference> parents(Cause cause) {
            return List.of();
        }

        @Override
        public List<? extends SubjectReference> parents(Set<Context> contexts) {
            return List.of();
        }

        @Override
        public Optional<String> option(String key, Cause cause) {
            return Optional.empty();
        }

        @Override
        public Optional<String> option(String key, Set<Context> contexts) {
            return Optional.empty();
        }

        @Override
        public String identifier() {
            return this.identifier;
        }
    }
}
