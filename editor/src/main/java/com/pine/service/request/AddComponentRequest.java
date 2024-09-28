package com.pine.service.request;

import com.pine.component.EntityComponent;
import com.pine.repository.Message;
import com.pine.repository.MessageSeverity;
import com.pine.repository.WorldRepository;
import com.pine.service.world.WorldService;

public class AddComponentRequest extends AbstractRequest {
    private final Class<? extends EntityComponent> componentClass;
    private final int entityId;

    public AddComponentRequest(Class<? extends EntityComponent> componentClass, int entityId) {
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
