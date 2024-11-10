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
import com.pine.service.streaming.impl.TextureService;
import com.pine.service.streaming.ref.TextureResourceRef;
import com.pine.tasks.StreamingTask;
import com.pine.tasks.SyncTask;

import java.util.Collections;
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
    public TextureService textureService;

    @PInject
    public Engine engine;

    public static final int MAX_TIMEOUT = 1000;

    private long sinceLastCleanup;

    public AbstractResourceRef<?> streamIn(String id, StreamableResourceType type) {
        if (id == null || repository.toStreamIn.containsKey(id)) {
            return null;
        }
        AbstractResourceRef<?> val = repository.streamed.get(id);
        if (val != null) {
            if (val.isLoaded()) {
                return val;
            }
        }
        if (!repository.discardedResources.containsKey(id)) {
            repository.toStreamIn.put(id, type);
            getLogger().warn("Requesting stream of {} of type {}", id, type);
        }
        return null;
    }

    public void streamOut(String id, StreamableResourceType type) {
        if (id == null || !repository.streamed.containsKey(id)) {
            return;
        }
        repository.toStreamOut.put(id, type);
        getLogger().warn("Streaming out {} of type {}", id, type);
    }

    @Override
    public void sync() {
        for (String resource : repository.streamData.keySet()) {
            AbstractResourceRef<?> ref = repository.streamed.get(resource);
            if (ref != null) {
                getLogger().warn("Loading streamed resource {} of type {}", resource, ref.getResourceType());
                ref.load(repository.streamData.get(resource));
            }
            repository.streamData.remove(resource);
            repository.toStreamIn.remove(resource);
        }
        disposeOfUnusedResources();
    }

    private void disposeOfUnusedResources() {
        if ((clock.totalTime - sinceLastCleanup) >= MAX_TIMEOUT) {
            sinceLastCleanup = clock.totalTime;
            getLogger().warn("Disposing of unused resources");
            for (AbstractResourceRef<?> resource : repository.streamed.values()) {
                if ((clock.totalTime - resource.lastUse) >= MAX_TIMEOUT) {
                    resource.dispose();
                    repository.streamed.remove(resource.id);
                }
            }

            for (String resourceId : repository.toStreamOut.keySet()) {
                var resource = repository.streamed.get(resourceId);
                if (resource != null) {
                    resource.dispose();
                    repository.streamed.remove(resource.id);
                }
            }
            repository.toStreamOut.clear();
        }
    }

    @Override
    public void dispose() {
        for (var resource : repository.streamed.values()) {
            if (!resource.isLoaded()) {
                continue;
            }
            resource.dispose();
        }
    }

    public TextureResourceRef streamTextureSync(String path) {
        var texture = textureService.stream(path, Collections.emptyMap(), Collections.emptyMap());
        if (texture != null) {
            String id = UUID.randomUUID().toString();
            var textureRef = new TextureResourceRef(id);
            textureRef.load(texture);
            repository.streamed.put(id, textureRef);
            return textureRef;
        }
        return null;
    }
}
