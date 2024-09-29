package com.pine.service.request;

import com.pine.Loggable;
import com.pine.Message;
import com.pine.MessageSeverity;
import com.pine.component.AbstractComponent;
import com.pine.component.Entity;
import com.pine.component.EntityComponent;
import com.pine.repository.WorldRepository;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class AddEntityRequest extends AbstractRequest implements Loggable {
    private final List<Class<? extends EntityComponent>> components;
    private Entity entity;

    public AddEntityRequest(List<Class<? extends EntityComponent>> components) {
        this.components = components;
    }

    @Override
    public Object getResponse() {
        return entity;
    }

    @Override
    public Message run(WorldRepository repository) {
        entity = new Entity();
        entity.parent = repository.rootEntity;
        repository.rootEntity.children.add(entity);
        try {
            AddComponentRequest.add(components, entity, repository);
            return new Message("Entity created successfully", MessageSeverity.SUCCESS);
        } catch (Exception e) {
            getLogger().error("Error while adding component", e);
            return new Message("Error while adding component", MessageSeverity.ERROR);
        }
    }
}
