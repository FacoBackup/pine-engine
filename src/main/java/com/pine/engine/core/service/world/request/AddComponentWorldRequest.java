package com.pine.engine.core.service.world.request;

import com.pine.common.messages.Message;
import com.pine.common.messages.MessageSeverity;
import com.pine.engine.core.component.AbstractComponent;
import com.pine.engine.core.component.EntityComponent;
import com.pine.engine.core.repository.WorldRepository;
import com.pine.engine.core.service.world.WorldService;

public class AddComponentWorldRequest extends AbstractWorldRequest {
    private final Class<EntityComponent> componentClass;
    private final int entityId;

    public AddComponentWorldRequest(Class<EntityComponent> componentClass, int entityId) {
        this.componentClass = componentClass;
        this.entityId = entityId;
    }

    @Override
    public Message run(WorldRepository repository, WorldService service) {
        if (!repository.entities.containsKey(entityId)) {
            return new Message("Entity not found", MessageSeverity.ERROR);
        }
        try {
            boolean isSuccess = repository.registerComponent(componentClass.getConstructor(Integer.class).newInstance(entityId));
            if(!isSuccess) {
                return new Message("Could not add component to entity", MessageSeverity.ERROR);
            }
        } catch (Exception e) {
            return new Message("Could not create", MessageSeverity.ERROR);
        }
        return new Message("Component added to entity", MessageSeverity.SUCCESS);
    }
}
