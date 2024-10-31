package com.pine.service.request;

import com.pine.messaging.Loggable;
import com.pine.repository.WorldRepository;
import com.pine.repository.streaming.StreamingRepository;

public abstract class AbstractRequest implements Loggable {
    public WorldRepository repository;
    public StreamingRepository streamingRepository;

    public abstract void run();

    public void setup(WorldRepository worldRepository, StreamingRepository streamingRepository) {
        this.repository = worldRepository;
        this.streamingRepository = streamingRepository;
    }
}
