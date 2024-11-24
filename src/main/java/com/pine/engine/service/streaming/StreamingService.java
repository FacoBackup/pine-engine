package com.pine.engine.service.streaming;

import com.pine.engine.Engine;
import com.pine.common.injection.Disposable;
import com.pine.common.injection.PBean;
import com.pine.common.injection.PInject;
import com.pine.common.messaging.Loggable;
import com.pine.engine.repository.ClockRepository;
import com.pine.engine.repository.streaming.AbstractResourceRef;
import com.pine.engine.repository.streaming.StreamableResourceType;
import com.pine.engine.repository.streaming.StreamingRepository;
import com.pine.engine.service.streaming.ref.TextureResourceRef;
import com.pine.engine.service.streaming.impl.TextureService;
import com.pine.engine.tasks.SyncTask;

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
            for (AbstractResourceRef<?> resource : repository.streamed.values()) {
                if ((clock.totalTime - resource.lastUse) >= MAX_TIMEOUT) {
                    resource.dispose();
                    repository.streamed.remove(resource.id);
                }
            }
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
