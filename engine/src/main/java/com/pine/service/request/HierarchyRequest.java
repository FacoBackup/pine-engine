package com.pine.service.request;

import com.pine.component.Entity;
import com.pine.messaging.Message;
import com.pine.messaging.MessageSeverity;
import com.pine.repository.WorldRepository;
import com.pine.repository.streaming.StreamingRepository;

import javax.annotation.Nullable;

public class HierarchyRequest extends AbstractRequest {
    private final Entity parent;
    private final Entity child;

    public HierarchyRequest(@Nullable Entity parent, Entity child) {
        this.parent = parent;
        this.child = child;
    }

    @Override
    public Message run(WorldRepository repository, StreamingRepository streamingRepository) {
        if (child.transformation.parent != null) {
            child.transformation.parent.children.remove(child.transformation);
        }
        child.transformation.parent = parent != null ? parent.transformation : repository.rootEntity.transformation;
        child.transformation.parent.children.add(child.transformation);
        return new Message("Entities linked successfully", MessageSeverity.SUCCESS);
    }
}
