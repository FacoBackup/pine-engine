package com.pine.component;

import com.pine.inspection.Inspectable;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractComponent<T extends EntityComponent> extends Inspectable implements EntityComponent {
    public final LinkedList<T> bag;

    public final Entity entity;
    public transient int changes = 0;
    public transient int frozenVersion = -1;

    public AbstractComponent(Entity entity, LinkedList<?> bag) {
        this.entity = entity;
        this.bag = (LinkedList<T>) bag;
        this.bag.add((T) this);
    }

    /**
     * Bean instance
     */
    public AbstractComponent() {
        entity = null;
        bag = new LinkedList<>();
    }

    final public LinkedList<T> getBag() {
        return bag;
    }

    @Override
    public Entity getEntity() {
        return entity;
    }

    @SuppressWarnings("unchecked")
    @Override
    final public void addComponent(EntityComponent instance) {
        bag.add((T) instance);
    }

    @Override
    public abstract Set<Class<? extends EntityComponent>> getDependencies();

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
