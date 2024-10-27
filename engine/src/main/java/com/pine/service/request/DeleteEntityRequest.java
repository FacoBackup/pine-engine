package com.pine.service.request;

import com.pine.messaging.Loggable;
import com.pine.messaging.Message;
import com.pine.messaging.MessageSeverity;
import com.pine.repository.WorldRepository;
import com.pine.repository.streaming.StreamingRepository;

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
    public Message run(WorldRepository repository, StreamingRepository streamingRepository) {
        for (String entityId : entities) {
            if (!Objects.equals(entityId, repository.rootEntity.id())) {
                String parent = repository.childParent.get(entityId);
                var parentList = repository.parentChildren.get(parent);
                if(parentList != null) {
                    parentList.remove(entityId);
                }
                repository.childParent.remove(entityId);

                removeComponentsHierarchically(entityId, repository);
                repository.parentChildren.remove(entityId);
            }
        }
        return new Message(entities.size() + " entities deleted", MessageSeverity.SUCCESS);
    }

    private void removeComponentsHierarchically(String entity, WorldRepository repository) {
        repository.unregisterComponents(entity);
        repository.entityMap.remove(entity);

        var children = repository.parentChildren.get(entity);
        if(children != null) {
            for (String c : children) {
                removeComponentsHierarchically(c, repository);
            }
        }
    }
}