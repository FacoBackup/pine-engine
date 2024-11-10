package com.pine.service.request;

import com.pine.component.ComponentType;
import com.pine.component.Entity;

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
        var world = hashGridService.getCurrentTile().getWorld();
        world.entityMap.put(entity.id(), entity);

        world.parentChildren.putIfAbsent(world.rootEntity.id(), new LinkedList<>());
        world.parentChildren.get(world.rootEntity.id()).add(entity.id());
        world.childParent.put(entity.id(), world.rootEntity.id());

        try {
            AddComponentRequest.add(components, entity, world);
            getLogger().warn("Entity created successfully");
        } catch (Exception e) {
            getLogger().error("Error while adding component", e);
        }
    }
}
