package com.pine.tasks;

import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.repository.streaming.StreamingRepository;
import com.pine.service.streaming.AbstractStreamableService;
import com.pine.service.streaming.StreamLoadData;

import java.util.List;

/**
 * Creates StreamLoadData for all the requests and inserts it inside the StreamingRepository.loadedResources map
 */
@PBean
public class StreamingTask extends AbstractTask implements Loggable {
    @PInject
    public StreamingRepository streamingRepository;

    @PInject
    public List<AbstractStreamableService> services;

    @Override
    protected int getTickIntervalMilliseconds() {
        return 1000;
    }

    @Override
    protected void tickInternal() {
        for (var scheduled : streamingRepository.schedule.values()) {
            for (var service : services) {
                if (service.getResourceType() == scheduled.getResourceType()) {
                    getLogger().warn("Streaming resource {}", scheduled.id);
                    StreamLoadData streamData = service.stream(scheduled.pathToFile);
                    if (streamData != null) {
                        streamingRepository.loadedResources.put(scheduled.id, streamData);
                    } else {
                        streamingRepository.schedule.remove(scheduled.id);
                        streamingRepository.loadedResources.remove(scheduled.id);
                        scheduled.invalidated = true;
                    }
                }
            }
        }
    }
}
