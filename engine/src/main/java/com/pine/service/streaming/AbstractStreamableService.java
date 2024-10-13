package com.pine.service.streaming;

import com.pine.Engine;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.repository.streaming.AbstractStreamableResource;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.streaming.mesh.MeshStreamData;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

public abstract class AbstractStreamableService<T extends AbstractStreamableResource<?>, C extends StreamLoadData> implements Loggable {

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

    public abstract C stream(String pathToFile);

    protected Object loadFile(String path) {
        try {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(engine.getResourceTargetDirectory() + path))) {
                return in.readObject();
            }
        } catch (Exception e) {
            getLogger().error(e.getMessage(), e);
        }
        return null;
    }
}
