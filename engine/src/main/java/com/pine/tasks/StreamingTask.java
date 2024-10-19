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
        for (var scheduled : streamingRepository.schedule.entrySet()) {
            for (var service : services) {
                if (service.getResourceType() == scheduled.getValue()) {
                    getLogger().warn("Streaming resource {}", scheduled);
                    StreamData streamData = service.stream(importerService.getPathToFile(scheduled.getKey(), scheduled.getValue()), streamingRepository.schedule);
                    if (streamData != null) {
                        streamingRepository.loadedResources.put(scheduled.getKey(), streamData);
                        if (!streamingRepository.streamableResources.containsKey(scheduled.getKey())) {
                            streamingRepository.streamableResources.put(scheduled.getKey(), service.newInstance(scheduled.getKey()));
                        }
                    } else {
                        streamingRepository.failedStreams.put(scheduled.getKey(), scheduled.getValue());
                        streamingRepository.schedule.remove(scheduled.getKey());
                        streamingRepository.loadedResources.remove(scheduled.getKey());
                    }
                }
            }
        }
    }
}
