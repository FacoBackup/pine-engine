package com.pine.repository.streaming;

import com.pine.injection.Disposable;
import com.pine.inspection.Inspectable;
import com.pine.service.streaming.StreamData;

import java.io.Serializable;

public abstract class AbstractResourceRef<T extends StreamData> implements Disposable {
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
            disposeInternal();
        }
    }

    public boolean isLoaded() {
        return loaded;
    }

    protected abstract void disposeInternal();
}
