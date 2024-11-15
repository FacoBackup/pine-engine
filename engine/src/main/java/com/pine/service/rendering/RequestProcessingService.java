package com.pine.service.rendering;

import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.repository.WorldRepository;
import com.pine.repository.streaming.StreamingRepository;
import com.pine.service.grid.WorldService;
import com.pine.service.request.AbstractRequest;


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
