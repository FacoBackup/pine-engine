package com.pine.service.request;

import com.pine.component.Entity;
import com.pine.messaging.Loggable;
import com.pine.messaging.Message;
import com.pine.messaging.MessageSeverity;
import com.pine.repository.WorldRepository;
import com.pine.repository.streaming.StreamingRepository;


public class AddEntityRequest extends AbstractRequest implements Loggable {
    private Entity entity;

    public AddEntityRequest() {
    }

    @Override
    public Message run(WorldRepository repository, StreamingRepository streamingRepository) {
        entity = new Entity();
        entity.transformation.parent = repository.rootEntity.transformation;
        repository.rootEntity.transformation.children.add(entity.transformation);
        return new Message("Entity created successfully", MessageSeverity.SUCCESS);
    }
}
