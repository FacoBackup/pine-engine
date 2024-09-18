package com.pine.core.service.resource.resource;

public abstract class AbstractResource implements IResource {
    protected final String id;
    protected boolean isStatic = false;

    public AbstractResource(final String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean aStatic) {
        isStatic = aStatic;
    }
}
