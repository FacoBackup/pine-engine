package com.pine.engine.resource;

public abstract class AbstractResource<C extends IResourceCreationData> implements IResource {
    protected final String id;

    public AbstractResource(final String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }
}
