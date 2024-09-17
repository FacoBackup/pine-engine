package com.pine.engine.core.component;

import com.pine.engine.core.service.serialization.SerializableResource;

import java.util.List;
import java.util.Set;
import java.util.Vector;

public abstract class AbstractComponent implements SerializableResource, EntityComponent {
    public transient final Vector<EntityComponent> bag = new Vector<>();
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

    @Override
    public Vector<EntityComponent> getBag() {
        return bag;
    }

    @Override
    final public Set<Class<? extends EntityComponent>> getDependencies(){
        var internal = getDependenciesInternal();
        internal.add(MetadataComponent.class);
        return internal;
    }

    protected abstract Set<Class<? extends EntityComponent>> getDependenciesInternal();

    @Override
    public String toString() {
        return getComponentName();
    }
}
