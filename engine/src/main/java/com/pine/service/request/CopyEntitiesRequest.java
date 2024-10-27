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

public class CopyEntitiesRequest extends AbstractRequest implements Loggable {
    private final List<String> entities;
    private final List<Entity> allCloned = new ArrayList<>();

    public CopyEntitiesRequest(Collection<String> entities) {
        this.entities = new ArrayList<>(entities);
    }

    public List<Entity> getAllCloned() {
        return allCloned;
    }

    @Override
    public Message run(WorldRepository repository, StreamingRepository streamingRepository) {
        for (String entityId : entities) {
            var entity = repository.entityMap.get(entityId);
            if (entity != repository.rootEntity) {
                try{
                    var cloned = entity.clone();
                    repository.entityMap.put(cloned.id(), cloned);
                    cloned.components.values().forEach(c -> {
                        repository.components.get(c.getType()).add(c);
                    });
                    allCloned.add(cloned);
                }catch (Exception e){
                    getLogger().error("Could not copy entity {}", entityId);
                }
            }
        }
        return new Message(entities.size() + " entities copied", MessageSeverity.SUCCESS);
    }
}