package com.pine.component;

import com.pine.inspection.FieldDTO;
import com.pine.inspection.FieldType;
import com.pine.inspection.MutableField;
import com.pine.service.serialization.SerializableResource;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;

public abstract class AbstractComponent<T extends EntityComponent> extends WithMutableData implements SerializableResource, EntityComponent {
    private transient final Vector<T> bag = new Vector<>();

    private final int entityId;

    public AbstractComponent() {
        entityId = -1;
    }

    public AbstractComponent(Integer entityId) {
        this.entityId = entityId;
    }

    final public int getEntityId() {
        return entityId;
    }

    final public Vector<T> getBag() {
        return bag;
    }

    @SuppressWarnings("unchecked")
    @Override
    final public void addComponent(EntityComponent instance) {
        bag.add((T) instance);
    }

    @Override
    final public Set<Class<? extends EntityComponent>> getDependencies() {
        var internal = new HashSet<>(getDependenciesInternal());
        internal.add(MetadataComponent.class);
        return internal;
    }

    protected abstract Set<Class<? extends EntityComponent>> getDependenciesInternal();

    @Override
    final public String toString() {
        return getComponentName();
    }


}
