package com.pine.engine.repository.streaming;

import com.pine.common.injection.Disposable;
import com.pine.common.messaging.Loggable;
import com.pine.engine.service.streaming.data.StreamData;

public abstract class AbstractResourceRef<T extends StreamData> implements Disposable, Loggable {
    public transient boolean loaded = false;
    public transient long lastUse;
    public final String id;
    public String name;

    public AbstractResourceRef(String id) {
        this.id = id;
    }

    public abstract StreamableResourceType getResourceType();

    @SuppressWarnings("unchecked")
    final public void load(StreamData data) {
        loadInternal((T) data);
        loaded = true;
    }

    protected abstract void loadInternal(T data);

    @Override
    final public void dispose() {
        if (loaded) {
            loaded = false;
            getLogger().warn("Disposing of resource {} of type {}", id, getResourceType());
            disposeInternal();
        }
    }

    public boolean isLoaded() {
        return loaded;
    }

    protected abstract void disposeInternal();
}
