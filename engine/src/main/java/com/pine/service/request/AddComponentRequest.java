package com.pine.service.request;

import com.pine.component.AbstractComponent;
import com.pine.component.ComponentType;
import com.pine.component.Entity;
import com.pine.messaging.Message;
import com.pine.messaging.MessageSeverity;
import com.pine.repository.WorldRepository;
import com.pine.repository.streaming.StreamingRepository;

import java.util.List;
import java.util.Set;

public class AddComponentRequest extends AbstractRequest {
    private final ComponentType type;
    private final Entity entity;

    public AddComponentRequest(ComponentType type, Entity entity) {
        this.type = type;
        this.entity = entity;
    }

    private static void addComponent(ComponentType type, Entity entity, WorldRepository repository) throws Exception {
        if (repository.components.get(type).containsKey(entity.id())) {
            return;
        }
        Class<? extends AbstractComponent> clazz = type.getClazz();
        var instance = clazz.getConstructor(String.class).newInstance(entity.id());
        repository.registerComponent(instance);
        Set<ComponentType> dependencies = instance.getDependencies();
        for (var dependency : dependencies) {
            addComponent(dependency, entity, repository);
        }
        repository.components.get(type).put(instance.getEntityId(), instance);
    }

    public static void add(List<ComponentType> components, Entity entity, WorldRepository repository) throws Exception {
        for (var type : components) {
            AddComponentRequest.addComponent(type, entity, repository);
        }
    }

    @Override
    public Message run(WorldRepository repository, StreamingRepository streamingRepository) {
        if (repository.components.get(type).containsKey(entity.id())) {
            return new Message("Entity already has component of type " + type.getTitle(), MessageSeverity.WARN);
        }
        try {
            AddComponentRequest.add(List.of(type), entity, repository);
        } catch (Exception e) {
            return new Message("Could not create", MessageSeverity.ERROR);
        }
        return new Message("Component added to entity", MessageSeverity.SUCCESS);
    }
}
