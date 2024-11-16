package io.github.pandier.atomi.sponge.internal;

import io.github.pandier.atomi.AtomiGroup;

public class GroupSubject extends AbstractEntitySubject {
    private final AtomiGroup group;
    private final NoParentEntitySubjectData data = new NoParentEntitySubjectData(this);

    public GroupSubject(AtomiGroup group, AbstractSubjectCollection collection) {
        super(group.name(), collection);
        this.group = group;
    }

    @Override
    public AtomiGroup entity() {
        return group;
    }

    @Override
    public AbstractEntitySubjectData subjectData() {
        return data;
    }
}
