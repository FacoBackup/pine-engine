package com.pine.tasks;

import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
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
    protected int getTickIntervalMilliseconds() {
        return 1000;
    }

    @Override
    protected void tickInternal() {
        try {
            for (var scheduled : streamingRepository.scheduleToLoad.entrySet()) {
                for (var service : services) {
                    if (service.getResourceType() == scheduled.getValue()) {
                        getLogger().warn("Streaming resource {} of type {}", scheduled.getKey(), scheduled.getValue());
                        StreamData streamData = service.stream(importerService.getPathToFile(scheduled.getKey(), scheduled.getValue()), streamingRepository.scheduleToLoad, streamingRepository.loadedResources);
                        if (streamData != null) {
                            streamingRepository.toLoadResources.put(scheduled.getKey(), streamData);
                            if (!streamingRepository.loadedResources.containsKey(scheduled.getKey())) {
                                streamingRepository.loadedResources.put(scheduled.getKey(), service.newInstance(scheduled.getKey()));
                            }
                        } else {
                            streamingRepository.discardedResources.put(scheduled.getKey(), scheduled.getValue());
                            streamingRepository.toLoadResources.remove(scheduled.getKey());
                        }
                        streamingRepository.scheduleToLoad.remove(scheduled.getKey());
                    }
                }
            }
        } catch (Exception e) {
            getLogger().error(e.getMessage(), e);
        }
    }
}
