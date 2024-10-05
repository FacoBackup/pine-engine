package com.pine.service.resource;

import com.pine.Loggable;

public abstract class AbstractResourceService<T extends IResource, C extends ResourceCreationData> implements Loggable {

    private String nextId;

    public void bind(IResource instance) {
        bindInternal((T) instance);
    }

    public IResource add(ResourceCreationData data, String fixedId) {
        this.nextId = fixedId;
        return addInternal((C) data);
    }

    public void remove(IResource resource) {
        removeInternal((T) resource);
    }

    protected abstract void unbind();

    protected abstract void bindInternal(T instance);

    protected abstract IResource addInternal(C data);

    protected abstract void removeInternal(T resource);

    public String getId() {
        return nextId;
    }

    public abstract LocalResourceType getResourceType();
}