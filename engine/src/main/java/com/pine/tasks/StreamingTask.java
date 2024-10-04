package com.pine.tasks;

import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.streaming.StreamingRepository;

/**
 * Creates StreamLoadData for all the requests and inserts it inside the StreamingRepository.loadedResources map
 */
@PBean
public class StreamingTask extends AbstractTask {
    @PInject
    public StreamingRepository streamingRepository;

    @Override
    protected int getTickIntervalMilliseconds() {
        return 1000;
    }

    @Override
    protected void tickInternal() {
        for (var scheduled : streamingRepository.schedule.values()) {
            switch (scheduled.getResourceType()) {
                case TEXTURE -> {

                }
                case AUDIO -> {

                }
                case MESH -> {

                }
            }
        }
    }
}
