package com.pine.service.request;

import com.pine.component.Entity;
import com.pine.component.Transformation;
import com.pine.messaging.Loggable;
import com.pine.messaging.Message;
import com.pine.messaging.MessageSeverity;
import com.pine.repository.WorldRepository;
import com.pine.repository.streaming.StreamingRepository;

import java.util.ArrayList;
import java.util.List;

public class DeleteEntityRequest extends AbstractRequest implements Loggable {
    private final List<Entity> entities;

    public DeleteEntityRequest(List<Entity> entities) {
        this.entities = new ArrayList<>(entities);
    }

    @Override
    public Message run(WorldRepository repository, StreamingRepository streamingRepository) {
        for (Entity entity : entities) {
            if (entity != repository.rootEntity) {
                Transformation t = entity.transformation;
                t.parent.children.remove(t);
                repository.unregisterComponents(entity);
            }
        }
        return new Message(entities.size() + " entities deleted", MessageSeverity.SUCCESS);
    }
}