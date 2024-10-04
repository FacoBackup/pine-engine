package com.pine.repository.streaming;

import com.pine.injection.Disposable;
import com.pine.service.streaming.StreamLoadData;

import java.util.UUID;

public abstract class AbstractStreamableResource<T extends StreamLoadData> implements Disposable {
    public transient boolean isLoaded = false;
    public final String id = UUID.randomUUID().toString();
    public String name;
    public transient long lastUse;

    public abstract StreamableResourceType getResourceType();

    @SuppressWarnings("unchecked")
    final public void load(StreamLoadData data) {
        loadInternal((T) data);
        isLoaded = true;
    }

    protected abstract void loadInternal(T data);

    @Override
    final public void dispose() {
        isLoaded = false;
        disposeInternal();
    }

    protected abstract void disposeInternal();
}
