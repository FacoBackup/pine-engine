package com.pine.service.streaming;

import com.pine.repository.streaming.AbstractStreamableResource;
import com.pine.repository.streaming.StreamableResourceType;

public abstract class AbstractStreamableService<T extends AbstractStreamableResource<?>> {
    protected T currentResource;

    public void bind(T instance) {
        if (currentResource != null && currentResource != instance) {
            unbind();
        }
        currentResource = instance;
        if (currentResource != null) {
            currentResource.lastUse = System.currentTimeMillis();
            bindInternal();
        }
    }

    protected abstract void bindInternal();

    public abstract void unbind();

    public abstract StreamableResourceType getResourceType();
}
