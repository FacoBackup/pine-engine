package com.pine.engine.core.component;

import com.pine.engine.core.service.serialization.SerializableResource;

import java.util.List;
import java.util.Set;
import java.util.Vector;

public abstract class AbstractComponent<T> implements SerializableResource {
    public transient final List<T> bag = new Vector<>();
    private final int entityId;

    public AbstractComponent() {
        entityId = -1;
    }

    public AbstractComponent(Integer entityId) {
        this.entityId = entityId;
    }

    public int getEntityId() {
        return entityId;
    }

    final public Set<Class<? extends AbstractComponent>> getDependencies(){
        var internal = getDependenciesInternal();
        internal.add(MetadataComponent.class);
        return internal;
    }

    protected abstract Set<Class<? extends AbstractComponent>> getDependenciesInternal();
}
