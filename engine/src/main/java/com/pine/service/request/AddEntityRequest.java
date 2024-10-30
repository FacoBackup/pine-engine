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
        repository.entityMap.put(entity.id(), entity);

        repository.parentChildren.putIfAbsent(repository.rootEntity.id(), new LinkedList<>());
        repository.parentChildren.get(repository.rootEntity.id()).add(entity.id());
        repository.childParent.put(entity.id(), repository.rootEntity.id());

        try {
            AddComponentRequest.add(components, entity, repository);
            getLogger().warn("Entity created successfully");
        } catch (Exception e) {
            getLogger().error("Error while adding component", e);
        }
    }
}
