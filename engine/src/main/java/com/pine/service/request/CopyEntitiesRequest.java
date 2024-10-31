package com.pine.service.request;

import com.pine.component.Entity;
import com.pine.messaging.Loggable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CopyEntitiesRequest extends AbstractRequest implements Loggable {
    private final List<String> entities;
    private final List<Entity> allCloned = new ArrayList<>();

    public CopyEntitiesRequest(Collection<String> entities) {
        this.entities = new ArrayList<>(entities);
    }

    public List<Entity> getAllCloned() {
        return allCloned;
    }

    @Override
    public void run() {
        for (String entityId : entities) {
            var entity = repository.entityMap.get(entityId);
            if (entity != repository.rootEntity) {
                try {
                    var cloned = entity.cloneEntity();
                    repository.entityMap.put(cloned.id(), cloned);

                    linkHierarchy(entityId, cloned);

                    cloneComponents(entityId, cloned);
                    allCloned.add(cloned);
                } catch (Exception e) {
                    getLogger().error("Could not copy entity {}", entityId);
                }
            }
        }
        getLogger().warn("{} entities copied", entities.size());
    }

    private  void cloneComponents(String entityId, Entity cloned) {
        repository.runByComponent((abstractComponent -> {
            repository.registerComponent(abstractComponent.cloneComponent(cloned));
        }), entityId);
    }

    private  void linkHierarchy(String entityId, Entity cloned) {
        String parent = repository.childParent.get(entityId);
        repository.parentChildren.get(parent).add(cloned.id());
        repository.childParent.put(cloned.id(), parent);
    }
}