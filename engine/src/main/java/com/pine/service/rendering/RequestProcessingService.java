package com.pine.service.rendering;

import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.repository.WorldRepository;
import com.pine.repository.streaming.StreamingRepository;
import com.pine.service.request.AbstractRequest;

import java.util.LinkedList;
import java.util.List;


@PBean
public class RequestProcessingService implements Loggable {
    private static final int MAX_HISTORY = 15;
    @PInject
    public WorldRepository worldRepository;

    @PInject
    public StreamingRepository streamingRepository;

    private final List<AbstractRequest> requests = new LinkedList<>();

    public void addRequest(AbstractRequest request) {
        if(requests.size() >= MAX_HISTORY){
            requests.removeFirst();
        }
        requests.add(request);

        try{
            request.setup(worldRepository, streamingRepository);
            request.run();
            worldRepository.registerChange();
        }catch (Exception e){
            getLogger().error(e.getMessage(), e);
        }
    }
}
