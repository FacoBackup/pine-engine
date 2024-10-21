package com.pine.service.streaming;

import com.pine.Engine;
import com.pine.injection.Disposable;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.repository.ClockRepository;
import com.pine.repository.streaming.AbstractResourceRef;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.repository.streaming.StreamingRepository;
import com.pine.tasks.SyncTask;

/**
 * Responsible for instantiating resources, creating requests for loading resources and disposing of loaded resources
 */
@PBean
public class StreamingService implements Loggable, SyncTask, Disposable {
    @PInject
    public StreamingRepository repository;

    @PInject
    public ClockRepository clock;

    @PInject
    public Engine engine;

    public static final int MAX_TIMEOUT = 60 * 1000;

    private long sinceLastCleanup;

    public AbstractResourceRef<?> stream(String id, StreamableResourceType type) {
        if (id == null || repository.scheduleToLoad.containsKey(id)) {
            return null;
        }
        AbstractResourceRef<?> val = repository.loadedResources.get(id);
        if (val != null) {
            if (val.isLoaded()) {
                return val;
            }
        }
        if (!repository.discardedResources.containsKey(id)) {
            repository.scheduleToLoad.put(id, type);
            getLogger().warn("Requesting stream of {} of type {}", id, type);
        }
        return null;
    }

    @Override
    public void sync() {
        for (String resource : repository.toLoadResources.keySet()) {
            AbstractResourceRef<?> ref = repository.loadedResources.get(resource);
            if (ref != null) {
                getLogger().warn("Loading streamed resource {} of type {}", resource, ref.getResourceType());
                ref.load(repository.toLoadResources.get(resource));
            }
            repository.toLoadResources.remove(resource);
            repository.scheduleToLoad.remove(resource);
        }
        disposeOfUnusedResources();
    }

    private void disposeOfUnusedResources() {
        if ((clock.totalTime - sinceLastCleanup) >= MAX_TIMEOUT) {
            sinceLastCleanup = clock.totalTime;
            getLogger().warn("Disposing of unused resources");
            for (AbstractResourceRef<?> resource : repository.loadedResources.values()) {
                if ((clock.totalTime - resource.lastUse) >= MAX_TIMEOUT) {
                    resource.dispose();
                    repository.loadedResources.remove(resource.id);
                }
            }
        }
    }

    @Override
    public void dispose() {
        for (var resource : repository.loadedResources.values()) {
            if (!resource.isLoaded()) {
                continue;
            }
            resource.dispose();
        }
    }
}
