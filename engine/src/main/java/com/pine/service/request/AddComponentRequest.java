package com.pine.service.request;

import com.pine.component.ComponentType;
import com.pine.component.Entity;
import com.pine.service.grid.TileWorld;

import java.util.List;

public class AddComponentRequest extends AbstractRequest {
    private final ComponentType type;
    private final Entity entity;

    public AddComponentRequest(ComponentType type, Entity entity) {
        this.type = type;
        this.entity = entity;
    }

    private static void addComponent(ComponentType type, Entity entity, TileWorld world) throws Exception {
        if (world.getBagByType(type).containsKey(entity.id())) {
            return;
        }

        var instance = type.getClazz().getConstructor(String.class).newInstance(entity.id());
        world.registerComponent(instance);
        for (var dependency : instance.getDependencies()) {
            addComponent(dependency, entity, world);
        }
    }

    public static void add(List<ComponentType> components, Entity entity, TileWorld world) throws Exception {
        for (var type : components) {
            AddComponentRequest.addComponent(type, entity, world);
        }
    }

    @Override
    public void run() {
        for (var tile : hashGridService.getLoadedTiles()) {
            if (tile != null && tile.getWorld().entityMap.containsKey(entity.id())) {
                if (tile.getWorld().getBagByType(type).containsKey(entity.id())) {
                    getLogger().warn("Component {} already exists on entity {}", type.getTitle(), entity.id());
                }
                try {
                    AddComponentRequest.add(List.of(type), entity, tile.getWorld());
                } catch (Exception e) {
                    getLogger().error("Error while adding component {}", type.getTitle(), e);
                }
            }
        }
    }
}
