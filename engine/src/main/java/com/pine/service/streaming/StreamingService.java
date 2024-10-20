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
        if (id == null || repository.schedule.containsKey(id)) {
            return null;
        }
        AbstractResourceRef<?> val = repository.streamableResources.get(id);
        if (val != null) {
            if (val.isLoaded()) {
                return val;
            }
        }
        if (!repository.failedStreams.containsKey(id)) {
            repository.schedule.put(id, type);
            getLogger().warn("Requesting stream of {} of type {}", id, type);
        }
        return null;
    }

    @Override
    public void sync() {
        for (String resource : repository.loadedResources.keySet()) {
            AbstractResourceRef<?> ref = repository.streamableResources.get(resource);
            if (ref != null) {
                getLogger().warn("Loading streamed resource {} of type {}", resource, ref.getResourceType());
                ref.load(repository.loadedResources.get(resource));
            }
            repository.loadedResources.remove(resource);
            repository.schedule.remove(resource);
        }
        disposeOfUnusedResources();
    }

    private void disposeOfUnusedResources() {
        if ((clock.totalTime - sinceLastCleanup) >= MAX_TIMEOUT) {
            sinceLastCleanup = clock.totalTime;
            getLogger().warn("Disposing of unused resources");
            for (AbstractResourceRef<?> resource : repository.streamableResources.values()) {
                if ((clock.totalTime - resource.lastUse) >= MAX_TIMEOUT) {
                    resource.dispose();
                    repository.streamableResources.remove(resource.id);
                }
            }
        }
    }

    @Override
    public void dispose() {
        for (var resource : repository.streamableResources.values()) {
            if (!resource.isLoaded()) {
                continue;
            }
            resource.dispose();
        }
    }
}
