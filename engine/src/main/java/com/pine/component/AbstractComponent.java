package com.pine.component;

import com.pine.Mutable;
import com.pine.SerializableRepository;
import com.pine.inspection.Inspectable;

import java.util.Set;

public abstract class AbstractComponent extends Inspectable implements Mutable, Cloneable, SerializableRepository {
    private String entityId;
    private int changes = 0;
    private int frozenVersion = -1;

    public AbstractComponent(String entityId) {
        this.entityId = entityId;
    }

    final public String getEntityId() {
        return entityId;
    }

    public Set<ComponentType> getDependencies() {
        return Set.of();
    }

    @Override
    final public int getChangeId() {
        return changes;
    }

    @Override
    final public void registerChange() {
        changes = (int) (Math.random() * 10000);
    }

    @Override
    final public boolean isNotFrozen() {
        return frozenVersion != getChangeId();
    }

    @Override
    final public void freezeVersion() {
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
            clone.entityId = entity.id;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
