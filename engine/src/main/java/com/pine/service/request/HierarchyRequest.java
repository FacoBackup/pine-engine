package com.pine.service.request;

import com.pine.component.Entity;

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
    public void run() {
        String previousParent = repository.childParent.get(child.id());
        repository.parentChildren.get(previousParent).remove(child.id());
        String newParent =  parent != null ? parent.id() : repository.rootEntity.id();
        repository.childParent.put(child.id(), newParent);
        repository.parentChildren.putIfAbsent(newParent, new LinkedList<>());
        repository.parentChildren.get(newParent).add(child.id());
        getLogger().warn("Entity {} linked to {}", child.id, parent == null ? null : parent.id);
    }
}
