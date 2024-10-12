package com.pine.component;

import com.pine.inspection.Inspectable;

import java.util.LinkedList;
import java.util.Set;

public abstract class AbstractComponent<T extends EntityComponent> extends Inspectable implements EntityComponent {
    public final LinkedList<T> bag;

    public final Entity entity;
    public int changes = 0;
    public int frozenVersion = -1;

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
    public Set<Class<? extends EntityComponent>> getDependencies() {
        return Set.of();
    }

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
        changes = (int) (Math.random() * 10000);
    }

    @Override
    public boolean isNotFrozen() {
        return frozenVersion != getChangeId();
    }

    @Override
    public void freezeVersion() {
        frozenVersion = getChangeId();
    }
}
