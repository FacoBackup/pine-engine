package com.pine.service.request;

import com.pine.component.Entity;
import com.pine.component.EntityComponent;
import com.pine.messaging.Loggable;
import com.pine.messaging.Message;
import com.pine.messaging.MessageSeverity;
import com.pine.repository.WorldRepository;

import java.util.List;


public class AddEntityRequest extends AbstractRequest implements Loggable {
    private final List<Class<? extends EntityComponent>> components;
    private Entity entity;

    public AddEntityRequest(List<Class<? extends EntityComponent>> components) {
        this.components = components;
    }

    @Override
    public Object getResponse() {
        return entity;
    }

    @Override
    public Message run(WorldRepository repository) {
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
