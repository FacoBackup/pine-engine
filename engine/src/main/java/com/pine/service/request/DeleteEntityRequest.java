package com.pine.service.request;

import com.pine.messaging.Loggable;
import com.pine.repository.WorldRepository;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class DeleteEntityRequest extends AbstractRequest implements Loggable {
    private final List<String> entities;
    private final Vector3f translationAux = new Vector3f();

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
        var transform = world.bagTransformationComponent.get(entityId);
        if(transform != null){
            transform.modelMatrix.getTranslation(translationAux);
            var tile = worldService.getHashGrid().getOrCreateTile(translationAux);
            tile.getEntities().remove(entityId);
        }

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