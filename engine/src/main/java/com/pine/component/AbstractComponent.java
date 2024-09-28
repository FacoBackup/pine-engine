package com.pine.component;

import com.pine.inspection.Inspectable;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public abstract class AbstractComponent<T extends EntityComponent> extends Inspectable implements EntityComponent {
    private transient final Vector<T> bag = new Vector<>();

    private final int entityId;
    private int changes = 0;
    private int frozenVersion = -1;

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
        return this.getTitle();
    }

    @Override
    public int getChangeId() {
        return changes;
    }

    @Override
    public void registerChange() {
        changes++;
    }

    @Override
    public boolean isFrozen() {
        return frozenVersion == getChangeId();
    }

    @Override
    public void freezeVersion() {
        frozenVersion = getChangeId();
    }
}
