package com.pine.service;

import com.pine.*;
import com.pine.repository.WorldRepository;
import com.pine.service.request.AbstractRequest;

import java.util.LinkedList;
import java.util.List;


@PBean
public class RequestProcessingService implements Loggable {
    private static final int MAX_HISTORY = 15;
    @PInject
    public WorldRepository worldRepository;

    @PInject
    public MessageRepository messageRepository;

    private final List<AbstractRequest> requests = new LinkedList<>();

    public void addRequest(AbstractRequest request) {
        if(requests.size() >= MAX_HISTORY){
            requests.removeFirst();
        }
        requests.add(request);

        try{
            Message message = request.run(worldRepository);
            if (message != null) {
                messageRepository.pushMessage(message);
            }
            worldRepository.registerChange();
        }catch (Exception e){
            getLogger().error(e.getMessage(), e);
        }
    }
}
