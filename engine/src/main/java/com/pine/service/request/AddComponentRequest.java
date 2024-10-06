package com.pine.service.request;

import com.pine.messaging.Message;
import com.pine.messaging.MessageSeverity;
import com.pine.component.AbstractComponent;
import com.pine.component.Entity;
import com.pine.component.EntityComponent;
import com.pine.repository.WorldRepository;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class AddComponentRequest extends AbstractRequest {
    private final Class<? extends EntityComponent> componentClass;
    private final Entity entity;

    public AddComponentRequest(Class<? extends EntityComponent> componentClass, Entity entity) {
        this.componentClass = componentClass;
        this.entity = entity;
    }

    private static void addComponent(Class<? extends EntityComponent> clazz, Entity entity, WorldRepository repository) throws Exception {
        if (entity.components.containsKey(clazz.getSimpleName())) {
            return;
        }
        var bean = (AbstractComponent<?>) repository.injector.getBean(clazz);
        var instance = clazz.getConstructor(Entity.class, LinkedList.class).newInstance(entity, bean.getBag());
        Set<Class<? extends EntityComponent>> dependencies = instance.getDependencies();
        for (var dependency : dependencies) {
            addComponent(dependency, entity, repository);
        }
        entity.components.put(clazz.getSimpleName(), instance);
    }

    public static void add(List<Class<? extends EntityComponent>> components, Entity entity, WorldRepository repository) throws Exception {
        for (Class<? extends EntityComponent> component : components) {
            AddComponentRequest.addComponent(component, entity, repository);
        }
    }

    @Override
    public Message run(WorldRepository repository) {
        if (entity.components.containsKey(componentClass.getSimpleName())) {
            return new Message("Entity already has component of type " + componentClass.getSimpleName(), MessageSeverity.WARN);
        }
        try {
            AddComponentRequest.add(List.of(componentClass), entity, repository);
        } catch (Exception e) {
            return new Message("Could not create", MessageSeverity.ERROR);
        }
        return new Message("Component added to entity", MessageSeverity.SUCCESS);
    }
}
