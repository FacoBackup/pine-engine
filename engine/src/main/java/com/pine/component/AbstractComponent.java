package com.pine.component;

import com.pine.Mutable;
import com.pine.SerializableRepository;
import com.pine.inspection.Inspectable;

import java.util.Set;

public abstract class AbstractComponent extends Inspectable implements Mutable, Cloneable, SerializableRepository {
    public Entity entity;
    public int changes = 0;
    public int frozenVersion = -1;

    public AbstractComponent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public Set<ComponentType> getDependencies() {
        return Set.of();
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

    abstract public ComponentType getType();

    @Override
    public String getTitle() {
        return getType().getTitle();
    }

    @Override
    public String getIcon() {
        return getType().getIcon();
    }

    public AbstractComponent cloneComponent(Entity entity) {
        try {
            var clone = (AbstractComponent) super.clone();
            clone.entity = entity;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
