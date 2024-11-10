package com.pine.service.request;

import com.pine.messaging.Loggable;
import com.pine.repository.streaming.StreamingRepository;
import com.pine.service.grid.HashGridService;

public abstract class AbstractRequest implements Loggable {
    public HashGridService hashGridService;
    public StreamingRepository streamingRepository;

    public abstract void run();

    public void setup(HashGridService hashGridService, StreamingRepository streamingRepository) {
        this.hashGridService = hashGridService;
        this.streamingRepository = streamingRepository;
    }
}
