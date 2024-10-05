package com.pine.service.resource;

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

    @Override
    public void makeStatic() {
        isStatic = true;
    }
}
