package com.pine.engine.service.streaming.impl;

import com.pine.common.injection.PInject;
import com.pine.common.messaging.Loggable;
import com.pine.engine.Engine;
import com.pine.engine.repository.streaming.AbstractResourceRef;
import com.pine.engine.repository.streaming.StreamableResourceType;
import com.pine.engine.service.streaming.data.StreamData;

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
