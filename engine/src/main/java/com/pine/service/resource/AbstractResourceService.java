package com.pine.service.resource;

import com.pine.Engine;
import com.pine.injection.Disposable;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.repository.RuntimeRepository;
import com.pine.service.resource.fbo.FBO;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractResourceService<T extends AbstractEngineResource, C extends IResourceCreationData> implements Loggable, Disposable {
    protected final Map<String, T> resources = new HashMap<>();

    @PInject
    public Engine engine;

    @PInject
    public RuntimeRepository runtimeRepository;

    public T create(C data) {
        T n = createInternal(data);
        resources.put(n.id, n);
        return n;
    }

    protected abstract T createInternal(C data);

    protected abstract void unbind();

    public abstract void bind(T instance);

    public void dispose(T instance) {
        resources.remove(instance.id);
        instance.dispose();
    }

    @Override
    public void dispose() {
        for(T resource : resources.values()) {
            resource.dispose();
        }
        resources.clear();
    }
}
