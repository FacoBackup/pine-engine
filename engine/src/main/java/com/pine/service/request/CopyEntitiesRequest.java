package com.pine.service.request;

import com.pine.component.Entity;
import com.pine.messaging.Loggable;
import com.pine.repository.WorldRepository;

import java.util.*;

public class CopyEntitiesRequest extends AbstractRequest implements Loggable {
    private final List<String> entities;
    private final List<Entity> allCloned = new ArrayList<>();
    private String parentId;

    public CopyEntitiesRequest(Collection<String> entities, String parentId) {
        this.entities = new ArrayList<>(entities);
        this.parentId = parentId;
    }

    public List<Entity> getAllCloned() {
        return allCloned;
    }

    @Override
    public void run() {
        if (parentId == null || !world.entityMap.containsKey(parentId)) {
            parentId = WorldRepository.ROOT_ID;
        }

        for (String entityId : entities) {
            clone(entityId, parentId);
        }
        getLogger().warn("{} entities copied", entities.size());
    }

    private void clone(String entityId, String parent) {
        var entity = world.entityMap.get(entityId);
        if (!Objects.equals(entity.id(), WorldRepository.ROOT_ID)) {
            try {
                var cloned = entity.cloneEntity();
                world.entityMap.put(cloned.id(), cloned);

                linkHierarchy(parent, cloned);

                cloneComponents(entityId, cloned);
                allCloned.add(cloned);
                var children = world.parentChildren.get(entityId);
                if (children != null) {
                    for (var child : children) {
                        clone(child, cloned.id);
                    }
                }
            } catch (Exception e) {
                getLogger().error("Could not copy entity {}", entityId, e);
            }
        }
    }

    private void cloneComponents(String entityId, Entity cloned) {
        world.runByComponent((abstractComponent -> {
            world.registerComponent(abstractComponent.cloneComponent(cloned));
        }), entityId);
    }

    private void linkHierarchy(String parent, Entity cloned) {
        world.parentChildren.putIfAbsent(parent, new LinkedList<>());
        world.parentChildren.get(parent).add(cloned.id());
        world.childParent.put(cloned.id(), parent);
    }
}