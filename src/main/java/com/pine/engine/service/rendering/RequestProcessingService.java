package com.pine.engine.service.rendering;

import com.pine.common.injection.PBean;
import com.pine.common.injection.PInject;
import com.pine.common.messaging.Loggable;
import com.pine.engine.repository.WorldRepository;
import com.pine.engine.repository.streaming.StreamingRepository;
import com.pine.engine.service.world.WorldService;
import com.pine.engine.service.request.AbstractRequest;


@PBean
public class RequestProcessingService implements Loggable {
    @PInject
    public WorldRepository world;

    @PInject
    public WorldService worldService;

    @PInject
    public StreamingRepository streamingRepository;

    public void addRequest(AbstractRequest request) {
        try{
            request.setup(world, streamingRepository, worldService);
            request.run();
        }catch (Exception e){
            getLogger().error(e.getMessage(), e);
        }
    }
}
