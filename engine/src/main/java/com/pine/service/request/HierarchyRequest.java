package com.pine.service.request;

import com.pine.component.Entity;
import com.pine.Message;
import com.pine.MessageSeverity;
import com.pine.repository.WorldRepository;

import javax.annotation.Nullable;
import java.util.Objects;

public class HierarchyRequest extends AbstractRequest {
    private final Entity parent;
    private final Entity child;

    public HierarchyRequest(@Nullable Entity parent, Entity child) {
        this.parent = parent;
        this.child = child;
    }

    @Override
    public Message run(WorldRepository repository) {
        if (child.parent != null) {
            child.parent.children.remove(child);
        }
        child.parent = Objects.requireNonNullElseGet(parent, () -> repository.rootEntity);
        child.parent.children.add(child);
        return new Message("Entities linked successfully", MessageSeverity.SUCCESS);
    }
}
