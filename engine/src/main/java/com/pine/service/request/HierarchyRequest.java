package com.pine.service.request;

import com.pine.component.Entity;
import com.pine.messaging.Message;
import com.pine.messaging.MessageSeverity;
import com.pine.repository.WorldRepository;
import com.pine.repository.streaming.StreamingRepository;

import javax.annotation.Nullable;
import java.util.LinkedList;

public class HierarchyRequest extends AbstractRequest {
    private final Entity parent;
    private final Entity child;

    public HierarchyRequest(@Nullable Entity parent, Entity child) {
        this.parent = parent;
        this.child = child;
    }

    @Override
    public Message run(WorldRepository repository, StreamingRepository streamingRepository) {
        String previousParent = repository.childParent.get(child.id());
        repository.parentChildren.get(previousParent).remove(child.id());
        String newParent =  parent != null ? parent.id() : repository.rootEntity.id();
        repository.childParent.put(child.id(), newParent);
        repository.parentChildren.putIfAbsent(newParent, new LinkedList<>());
        repository.parentChildren.get(newParent).add(child.id());
        return new Message("Entities linked successfully", MessageSeverity.SUCCESS);
    }
}
