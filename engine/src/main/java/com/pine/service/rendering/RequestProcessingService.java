package com.pine.service.rendering;

import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.repository.streaming.StreamingRepository;
import com.pine.service.grid.HashGridService;
import com.pine.service.request.AbstractRequest;


@PBean
public class RequestProcessingService implements Loggable {
    @PInject
    public HashGridService hashGridService;

    @PInject
    public StreamingRepository streamingRepository;

    public void addRequest(AbstractRequest request) {
        try{
            request.setup(hashGridService, streamingRepository);
            request.run();
        }catch (Exception e){
            getLogger().error(e.getMessage(), e);
        }
    }
}
