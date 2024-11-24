package com.pine.engine.service.request;

import com.pine.common.messaging.Loggable;
import com.pine.engine.repository.WorldRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class DeleteEntityRequest extends AbstractRequest implements Loggable {
    private final List<String> entities;

    public DeleteEntityRequest(Collection<String> entities) {
        this.entities = new ArrayList<>(entities);
    }

    @Override
    public void run() {
        for (String entityId : entities) {
            if (!Objects.equals(entityId, WorldRepository.ROOT_ID)) {
                removeEntity(entityId);
            }
        }
        getLogger().warn("Deleted {} entities", entities.size());
    }

    public void removeEntity(String entityId) {
        String parent = world.childParent.get(entityId);
        var parentList = world.parentChildren.get(parent);
        if (parentList != null) {
            parentList.remove(entityId);
        }
        world.childParent.remove(entityId);

        removeComponentsHierarchically(entityId);
        world.parentChildren.remove(entityId);
    }

    private void removeComponentsHierarchically(String entity) {
        worldService.getTiles().values().forEach(t -> t.getEntitiesMap().remove(entity));

        world.unregisterComponents(entity);
        world.entityMap.remove(entity);

        var children = world.parentChildren.get(entity);
        if (children != null) {
            for (String c : children) {
                removeComponentsHierarchically(c);
            }
        }
    }
}