package com.pine.engine.service.request;

import com.pine.engine.component.ComponentType;
import com.pine.engine.component.Entity;
import com.pine.engine.repository.WorldRepository;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class AddEntityRequest extends AbstractRequest {
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
    public void run() {
        entity = new Entity();
        world.entityMap.put(entity.id(), entity);

        world.parentChildren.putIfAbsent(WorldRepository.ROOT_ID, new LinkedList<>());
        world.parentChildren.get(WorldRepository.ROOT_ID).add(entity.id());
        world.childParent.put(entity.id(), WorldRepository.ROOT_ID);

        try {
            AddComponentRequest.add(components, entity, world);
            getLogger().warn("Entity created successfully");
        } catch (Exception e) {
            getLogger().error("Error while adding component", e);
        }
    }
}
