package io.github.pandier.atomi.sponge.internal;

import io.github.pandier.atomi.AtomiUser;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class UserSubject extends AbstractEntitySubject {
    private final AtomiUser user;
    private final UserSubjectData data = new UserSubjectData(this);

    public UserSubject(AtomiUser user, AbstractSubjectCollection collection) {
        super(user.uuid().toString(), collection);
        this.user = user;
    }

    @Override
    public AtomiUser entity() {
        return user;
    }

    @Override
    public AbstractEntitySubjectData subjectData() {
        return data;
    }
}
