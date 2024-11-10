package com.pine.service.request;

import com.pine.messaging.Loggable;
import com.pine.service.grid.Tile;
import com.pine.service.grid.TileWorld;

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
            if (!Objects.equals(entityId, TileWorld.ROOT_ID)) {
                for(var tile : hashGridService.getLoadedTiles()){
                    if(tile != null && tile.getWorld().entityMap.containsKey(entityId)){
                        removeEntity(entityId, tile);
                    }
                }
            }
        }

        getLogger().warn("Deleted {} entities", entities.size());
    }

    public static void removeEntity(String entityId, Tile tile) {
        String parent = tile.getWorld().childParent.get(entityId);
        var parentList = tile.getWorld().parentChildren.get(parent);
        if(parentList != null) {
            parentList.remove(entityId);
        }
        tile.getWorld().childParent.remove(entityId);

        removeComponentsHierarchically(entityId, tile.getWorld());
        tile.getWorld().parentChildren.remove(entityId);
    }

    private static void removeComponentsHierarchically(String entity, TileWorld world) {
        world.unregisterComponents(entity);
        world.entityMap.remove(entity);

        var children = world.parentChildren.get(entity);
        if(children != null) {
            for (String c : children) {
                removeComponentsHierarchically(c, world);
            }
        }
    }
}