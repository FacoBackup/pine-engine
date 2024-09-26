package com.pine.service;

import com.pine.Loggable;
import com.pine.PBean;
import com.pine.PInject;
import com.pine.repository.WorldRepository;
import com.pine.service.world.WorldService;
import com.pine.service.world.request.AbstractRequest;
import com.pine.service.world.request.RequestMessage;
import com.pine.tasks.SyncTask;

import java.util.Vector;


@PBean
public class RequestProcessingService implements SyncTask, Loggable {
    @PInject
    public WorldRepository worldRepository;

    @PInject
    public WorldService world;

    @PInject
    public MessageService messageService;

    private final Vector<AbstractRequest> requests = new Vector<>();

    @Override
    public void sync() {
        if (requests.isEmpty()) {
            return;
        }
        try {
            for (var request : requests) {
                RequestMessage message = request.run(worldRepository, world);
                if (message != null) {
                    messageService.onMessage(message.message(), message.isError());
                }
            }
            requests.clear();
            worldRepository.registerChange();
        } catch (Exception e) {
            getLogger().error(e.getMessage(), e);
        }
    }

    public void addRequest(AbstractRequest request) {
        requests.add(request);
    }
}
