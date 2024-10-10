package com.pine.service.request;

import com.pine.component.AbstractComponent;
import com.pine.component.Entity;
import com.pine.component.EntityComponent;
import com.pine.component.Transformation;
import com.pine.messaging.Loggable;
import com.pine.messaging.Message;
import com.pine.messaging.MessageSeverity;
import com.pine.repository.WorldRepository;

import java.util.ArrayList;
import java.util.List;

public class DeleteEntityRequest extends AbstractRequest implements Loggable {
    private final List<Entity> entities;

    public DeleteEntityRequest(List<Entity> entities) {
        this.entities = new ArrayList<>(entities);
    }

    @Override
    public Message run(WorldRepository repository) {
        for (Entity entity : entities) {
            if (entity != repository.rootEntity) {
                Transformation t = entity.transformation;
                t.parent.children.remove(t);
                for (var c : repository.allComponents) {
                    EntityComponent comp = entity.components.get(c.getClass().getSimpleName());
                    if (comp != null) {
                        ((AbstractComponent<?>) c).getBag().remove(comp);
                    }
                }
            }
        }
        return new Message(entities.size() + " entities deleted", MessageSeverity.SUCCESS);
    }
}