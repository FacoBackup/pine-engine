package com.pine.engine.core.service.resource.resource;

import com.pine.common.Initializable;
import com.pine.common.Loggable;
import com.pine.engine.Engine;
import com.pine.engine.core.service.EngineInjectable;

import java.util.List;
import java.util.UUID;

public abstract class AbstractResourceService<T extends IResource, R extends IResourceRuntimeData, C extends ResourceCreationData> implements Loggable, EngineInjectable {

    protected final Engine engine;

    public AbstractResourceService(Engine engine) {
        this.engine = engine;
    }

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
