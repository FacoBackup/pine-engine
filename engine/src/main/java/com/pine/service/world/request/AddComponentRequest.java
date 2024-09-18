package com.pine.service.world.request;

import com.pine.component.EntityComponent;
import com.pine.repository.WorldRepository;
import com.pine.service.world.WorldService;

public class AddComponentRequest extends AbstractRequest {
    private final Class<EntityComponent> componentClass;
    private final int entityId;

    public AddComponentRequest(Class<EntityComponent> componentClass, int entityId) {
        this.componentClass = componentClass;
        this.entityId = entityId;
    }

    @Override
    public RequestMessage run(WorldRepository repository, WorldService service) {
        if (!repository.entities.containsKey(entityId)) {
            return new RequestMessage("Entity not found", true);
        }
        try {
            boolean isSuccess = repository.registerComponent(componentClass.getConstructor(Integer.class).newInstance(entityId));
            if(!isSuccess) {
                return new RequestMessage("Could not add component to entity", true);
            }
        } catch (Exception e) {
            return new RequestMessage("Could not create", true);
        }
        return new RequestMessage("Component added to entity", false);
    }
}
