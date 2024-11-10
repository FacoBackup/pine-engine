package com.pine.service.request;

import com.pine.component.Entity;
import com.pine.messaging.Loggable;
import com.pine.service.grid.TileWorld;

import java.util.*;

public class CopyEntitiesRequest extends AbstractRequest implements Loggable {
    private final List<String> entities;
    private final List<Entity> allCloned = new ArrayList<>();
    private final String parentId;

    public CopyEntitiesRequest(Collection<String> entities, String parentId) {
        this.entities = new ArrayList<>(entities);
        this.parentId = parentId;
    }

    public List<Entity> getAllCloned() {
        return allCloned;
    }

    @Override
    public void run() {
        for (String entityId : entities) {
            for (var tile : hashGridService.getLoadedTiles()) {
                if (tile != null && tile.getWorld().entityMap.containsKey(entityId)) {
                    String parentIdLocal = parentId;
                    if (parentIdLocal == null || !tile.getWorld().entityMap.containsKey(parentIdLocal)) {
                        parentIdLocal = tile.getWorld().rootEntity.id();
                    }

                    clone(tile.getWorld(), entityId, parentIdLocal);
                }
            }
        }
        getLogger().warn("{} entities copied", entities.size());
    }

    private void clone(TileWorld tile, String entityId, String parent) {
        var entity = tile.entityMap.get(entityId);
        if (!Objects.equals(entityId, TileWorld.ROOT_ID) && entity != null) {
            try {
                var cloned = entity.cloneEntity();
                tile.entityMap.put(cloned.id(), cloned);

                linkHierarchy(tile, parent, cloned);

                cloneComponents(tile, entityId, cloned);
                allCloned.add(cloned);
                var children = tile.parentChildren.get(entityId);
                if (children != null) {
                    for (var child : children) {
                        clone(tile, child, cloned.id);
                    }
                }
            } catch (Exception e) {
                getLogger().error("Could not copy entity {}", entityId, e);
            }
        }
    }

    private void cloneComponents(TileWorld tileWorld, String entityId, Entity cloned) {
        tileWorld.runByComponent((abstractComponent -> {
            tileWorld.registerComponent(abstractComponent.cloneComponent(cloned));
        }), entityId);
    }

    private void linkHierarchy(TileWorld world, String parent, Entity cloned) {
        world.parentChildren.putIfAbsent(parent, new LinkedList<>());
        world.parentChildren.get(parent).add(cloned.id());
        world.childParent.put(cloned.id(), parent);
    }
}