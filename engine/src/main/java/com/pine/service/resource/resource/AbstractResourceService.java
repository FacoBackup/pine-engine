package com.pine.service.resource.resource;

import com.pine.Loggable;

import java.util.List;
import java.util.UUID;

public abstract class AbstractResourceService<T extends IResource, R extends IResourceRuntimeData, C extends ResourceCreationData> implements Loggable {

    public void bind(IResource instance, IResourceRuntimeData data) {
        bindInternal((T) instance, (R) data);
    }

    public void bind(IResource instance) {
        bindInternal((T) instance);
    }

    public IResource add(ResourceCreationData data) {
        return addInternal((C) data);
    }

    public void remove(IResource resource) {
        removeInternal((T) resource);
    }

    protected abstract void unbind();

    protected abstract void bindInternal(T instance, R data);

    protected abstract void bindInternal(T instance);

    protected abstract IResource addInternal(C data);

    protected abstract void removeInternal(T resource);

    public String getId() {
        return UUID.randomUUID().toString();
    }

    public abstract ResourceType getResourceType();

    /**
     * Disposes of resources
     */
    public void shutdown(List<IResource> resources) {
    }
}