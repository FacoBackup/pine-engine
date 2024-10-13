package com.pine.repository.streaming;

import com.pine.injection.Disposable;
import com.pine.inspection.Inspectable;
import com.pine.service.streaming.StreamLoadData;

import java.io.Serializable;

public abstract class AbstractStreamableResource<T extends StreamLoadData> extends Inspectable implements Disposable, Serializable {
    public transient boolean loaded = false;
    public transient long lastUse;
    public final String id;
    public String name;
    public final String pathToFile;
    public float size;
    public boolean invalidated = false;

    public AbstractStreamableResource(String pathToFile, String id) {
        this.pathToFile = pathToFile;
        this.id = id;
    }

    public abstract StreamableResourceType getResourceType();

    @SuppressWarnings("unchecked")
    final public void load(StreamLoadData data) {
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

    @Override
    final public String getTitle() {
        return name;
    }
}
