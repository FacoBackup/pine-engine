package com.pine.service.resource;

import com.pine.messaging.Loggable;

public abstract class AbstractResourceService<T extends IResource> implements Loggable {
    protected abstract void unbind();

    public abstract void bind(T instance);
}
