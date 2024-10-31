package com.pine.service.request;

import com.pine.component.ComponentType;
import com.pine.component.Entity;
import com.pine.repository.WorldRepository;

import java.util.List;

public class AddComponentRequest extends AbstractRequest {
    private final ComponentType type;
    private final Entity entity;

    public AddComponentRequest(ComponentType type, Entity entity) {
        this.type = type;
        this.entity = entity;
    }

    private static void addComponent(ComponentType type, Entity entity, WorldRepository repository) throws Exception {
        if (repository.getBagByType(type).containsKey(entity.id())) {
            return;
        }

        var instance = type.getClazz().getConstructor(String.class).newInstance(entity.id());
        repository.registerComponent(instance);
        for (var dependency : instance.getDependencies()) {
            addComponent(dependency, entity, repository);
        }
    }

    public static void add(List<ComponentType> components, Entity entity, WorldRepository repository) throws Exception {
        for (var type : components) {
            AddComponentRequest.addComponent(type, entity, repository);
        }
    }

    @Override
    public void run() {
        if (repository.getBagByType(type).containsKey(entity.id())) {
            getLogger().warn("Component {} already exists on entity {}", type.getTitle(), entity.id());
        }
        try {
            AddComponentRequest.add(List.of(type), entity, repository);
        } catch (Exception e) {
            getLogger().error("Error while adding component {}", type.getTitle(), e);
        }
    }
}
