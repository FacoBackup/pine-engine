package com.pine.service.request;

import com.pine.component.Entity;
import com.pine.component.Transformation;
import com.pine.messaging.Loggable;
import com.pine.messaging.Message;
import com.pine.messaging.MessageSeverity;
import com.pine.repository.WorldRepository;
import com.pine.repository.streaming.StreamingRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DeleteEntityRequest extends AbstractRequest implements Loggable {
    private final List<String> entities;

    public DeleteEntityRequest(Collection<String> entities) {
        this.entities = new ArrayList<>(entities);
    }

    @Override
    public Message run(WorldRepository repository, StreamingRepository streamingRepository) {
        for (String entityId : entities) {
            var entity = repository.entityMap.get(entityId);
            if (entity != repository.rootEntity) {
                Transformation t = entity.transformation;
                t.parent.children.remove(t);
                removeComponentsHierarchically(entity, repository);
            }
        }
        return new Message(entities.size() + " entities deleted", MessageSeverity.SUCCESS);
    }

    private void removeComponentsHierarchically(Entity entity, WorldRepository repository) {
        repository.entityMap.remove(entity.id());
        repository.unregisterComponents(entity);
        entity.transformation.children.forEach(c -> {
            removeComponentsHierarchically(c.entity, repository);
        });
    }
}