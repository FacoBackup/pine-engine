package com.pine.service.streaming.impl;

import com.pine.Engine;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.repository.streaming.AbstractResourceRef;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.streaming.data.StreamData;

import java.util.Map;

public abstract class AbstractStreamableService<T extends AbstractResourceRef<?>> implements Loggable {

    @PInject
    public Engine engine;

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

    protected void bindInternal() {
    }

    public void unbind() {
    }

    public abstract StreamableResourceType getResourceType();

    public abstract StreamData stream(String pathToFile, Map<String, StreamableResourceType> schedule, Map<String, AbstractResourceRef<?>> streamableResources);

    public abstract AbstractResourceRef<?> newInstance(String key);
}
