package com.pine.tasks;

import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.repository.streaming.StreamingRepository;
import com.pine.service.importer.ImporterService;
import com.pine.service.streaming.AbstractStreamableService;
import com.pine.service.streaming.StreamData;

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
            for (var scheduled : streamingRepository.scheduleToLoad.entrySet()) {
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
            streamingRepository.scheduleToLoad.remove(id);
            return;
        }
        StreamData streamData = null;
        try {
            streamData = service.stream(importerService.getPathToFile(id, type), streamingRepository.scheduleToLoad, streamingRepository.loadedResources);
        }catch (Exception e){
            getLogger().error("Could not stream resource {}", id, e);
        }
        if (streamData != null) {
            streamingRepository.toLoadResources.put(id, streamData);
            if (!streamingRepository.loadedResources.containsKey(id)) {
                streamingRepository.loadedResources.put(id, service.newInstance(id));
            }
        } else {
            streamingRepository.discardedResources.put(id, type);
            streamingRepository.toLoadResources.remove(id);
        }
        streamingRepository.scheduleToLoad.remove(id);
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
