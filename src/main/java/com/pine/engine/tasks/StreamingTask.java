package com.pine.engine.tasks;

import com.pine.common.injection.PBean;
import com.pine.common.injection.PInject;
import com.pine.common.messaging.Loggable;
import com.pine.engine.repository.streaming.StreamableResourceType;
import com.pine.engine.repository.streaming.StreamingRepository;
import com.pine.engine.service.importer.ImporterService;
import com.pine.engine.service.streaming.data.StreamData;
import com.pine.engine.service.streaming.impl.AbstractStreamableService;

import java.util.List;

/**
 * Creates StreamLoadData for all the requests and inserts it inside the StreamingRepository.loadedResources map
 */
@PBean
public class StreamingTask extends AbstractTask implements Loggable {
    @PInject
    public StreamingRepository streamingRepository;

    @PInject
    public ImporterService importerService;

    @PInject
    public List<AbstractStreamableService> services;

    @Override
    protected void tickInternal() {
        streamAll();
    }

    public void streamAll() {
        startTracking();
        try {
            for (var scheduled : streamingRepository.toStreamIn.entrySet()) {
                stream(scheduled.getKey(), scheduled.getValue());
            }
        } catch (Exception e) {
            getLogger().error(e.getMessage(), e);
        }
        endTracking();
    }

    private void stream(String id, StreamableResourceType type) {
        getLogger().warn("Streaming resource {} of type {}", id, type);
        var service = getService(type);
        if(service == null){
            streamingRepository.toStreamIn.remove(id);
            return;
        }
        StreamData streamData = null;
        try {
            streamData = service.stream(importerService.getPathToFile(id, type), streamingRepository.toStreamIn, streamingRepository.streamed);
        }catch (Exception e){
            getLogger().error("Could not stream resource {}", id, e);
        }
        if (streamData != null) {
            streamingRepository.streamData.put(id, streamData);
            if (!streamingRepository.streamed.containsKey(id)) {
                streamingRepository.streamed.put(id, service.newInstance(id));
            }
        } else {
            streamingRepository.discardedResources.put(id, type);
            streamingRepository.streamData.remove(id);
        }
        streamingRepository.toStreamIn.remove(id);
    }

    private AbstractStreamableService getService(StreamableResourceType resourceType) {
        for (var service : services) {
            if (service.getResourceType() == resourceType) {
                return service;
            }
        }
        return null;
    }

    @Override
    public String getTitle() {
        return "Streaming";
    }
}
