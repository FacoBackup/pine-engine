package com.pine.service.request;

import com.pine.component.ComponentType;
import com.pine.component.Entity;
import com.pine.messaging.Loggable;
import com.pine.messaging.Message;
import com.pine.messaging.MessageSeverity;
import com.pine.repository.WorldRepository;
import com.pine.repository.streaming.StreamingRepository;

import java.util.Collections;
import java.util.List;


public class AddEntityRequest extends AbstractRequest implements Loggable {
    private final List<ComponentType> components;
    private Entity entity;

    public AddEntityRequest(List<ComponentType> components) {
        this.components = components;
    }

    public AddEntityRequest() {
        this.components = Collections.emptyList();
    }

    public Entity getResponse() {
        return entity;
    }

    @Override
    public Message run(WorldRepository repository, StreamingRepository streamingRepository) {
        entity = new Entity();
        entity.transformation.parent = repository.rootEntity.transformation;
        repository.rootEntity.transformation.children.add(entity.transformation);
        try {
            AddComponentRequest.add(components, entity, repository);
            return new Message("Entity created successfully", MessageSeverity.SUCCESS);
        } catch (Exception e) {
            getLogger().error("Error while adding component", e);
            return new Message("Error while adding component", MessageSeverity.ERROR);
        }
    }
}
