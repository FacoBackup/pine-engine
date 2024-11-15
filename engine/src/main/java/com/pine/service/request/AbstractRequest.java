package com.pine.service.request;

import com.pine.messaging.Loggable;
import com.pine.repository.WorldRepository;
import com.pine.repository.streaming.StreamingRepository;
import com.pine.service.grid.WorldService;

public abstract class AbstractRequest implements Loggable {
    public WorldRepository world;
    public StreamingRepository streamingRepository;
    public WorldService worldService;

    public abstract void run();

    public void setup(WorldRepository world, StreamingRepository streamingRepository, WorldService worldService) {
        this.world = world;
        this.streamingRepository = streamingRepository;
        this.worldService = worldService;
    }
}
