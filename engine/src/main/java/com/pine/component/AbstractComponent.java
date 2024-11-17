package com.pine.component;

import com.pine.inspection.Inspectable;

import java.util.Set;

public abstract class AbstractComponent extends Inspectable implements Cloneable {
    private String entityId;

    public AbstractComponent(String entityId) {
        this.entityId = entityId;
    }

    final public String getEntityId() {
        return entityId;
    }

    public Set<ComponentType> getDependencies() {
        return Set.of();
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
            clone.registerChange();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
