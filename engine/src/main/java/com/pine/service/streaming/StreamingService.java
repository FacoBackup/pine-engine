package com.pine.service.streaming;

import com.pine.Engine;
import com.pine.injection.Disposable;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.repository.ClockRepository;
import com.pine.repository.streaming.AbstractStreamableResource;
import com.pine.repository.streaming.StreamingRepository;
import com.pine.tasks.SyncTask;

import javax.annotation.Nullable;
import java.util.UUID;

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

    @Nullable
    public <T extends AbstractStreamableResource<?>> T addNew(Class<T> clazz, String name) {
        try {
            String id = UUID.randomUUID().toString();
            T newInstance = clazz.getConstructor(String.class, String.class).newInstance(id + ".dat", id);
            newInstance.name = name;
            repository.streamableResources.add(newInstance);
            return newInstance;
        } catch (Exception ex) {
            getLogger().error(ex.getMessage(), ex);
        }
        return null;
    }

    public void stream(AbstractStreamableResource<?> resource) {
        if (resource.isLoaded() || resource.invalidated) {
            return;
        }

        if (!repository.schedule.containsKey(resource.id)) {
            getLogger().warn("Scheduling load of resource {}", resource.id);
            repository.schedule.put(resource.id, resource);
        }
    }

    @Override
    public void sync() {
        for (AbstractStreamableResource<?> resource : repository.schedule.values()) {
            if (repository.loadedResources.containsKey(resource.id)) {
                getLogger().warn("Loading streamed resource {}", resource.id);
                resource.load(repository.loadedResources.get(resource.id));
                repository.loadedResources.remove(resource.id);
                repository.schedule.remove(resource.id);
            }
        }

        disposeOfUnusedResources();
    }

    private void disposeOfUnusedResources() {
        if ((clock.totalTime - sinceLastCleanup) >= MAX_TIMEOUT) {
            sinceLastCleanup = clock.totalTime;
            for (AbstractStreamableResource<?> resource : repository.streamableResources) {
                if ((clock.totalTime - resource.lastUse) >= MAX_TIMEOUT) {
                    resource.dispose();
                }
            }
        }
    }

    @Override
    public void dispose() {
        for (var resource : repository.streamableResources) {
            if (!resource.isLoaded()) {
                continue;
            }
            resource.dispose();
        }
    }
}
