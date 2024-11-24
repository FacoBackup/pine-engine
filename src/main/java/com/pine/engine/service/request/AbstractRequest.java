package com.pine.engine.service.request;

import com.pine.common.messaging.Loggable;
import com.pine.engine.repository.WorldRepository;
import com.pine.engine.repository.streaming.StreamingRepository;
import com.pine.engine.service.world.WorldService;

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
