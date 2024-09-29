package com.pine.service.request;

import com.pine.component.AbstractComponent;
import com.pine.component.Entity;
import com.pine.component.EntityComponent;
import com.pine.Message;
import com.pine.MessageSeverity;
import com.pine.repository.WorldRepository;

import java.util.LinkedList;
import java.util.List;

public class AddComponentRequest extends AbstractRequest {
    private final Class<? extends EntityComponent> componentClass;
    private final Entity entity;

    public AddComponentRequest(Class<? extends EntityComponent> componentClass, Entity entity) {
        this.componentClass = componentClass;
        this.entity = entity;
    }

    @Override
    public Message run(WorldRepository repository) {
        if(entity.components.containsKey(componentClass.getSimpleName())){
            return new Message("Entity already has component of type " + componentClass.getSimpleName(), MessageSeverity.WARN);
        }
        try {
            var bean = (AbstractComponent<?>) repository.injector.getBean(componentClass);
            entity.components.put(componentClass.getSimpleName(), componentClass.getConstructor(Entity.class, LinkedList.class).newInstance(entity, bean.getBag()));
        } catch (Exception e) {
            return new Message("Could not create", MessageSeverity.ERROR);
        }
        return new Message("Component added to entity", MessageSeverity.SUCCESS);
    }
}
