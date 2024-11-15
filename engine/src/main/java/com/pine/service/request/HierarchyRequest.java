package com.pine.service.request;

import com.pine.component.Entity;
import com.pine.repository.WorldRepository;

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
        String previousParent = world.childParent.get(child.id());
        world.parentChildren.get(previousParent).remove(child.id());
        String newParent =  parent != null ? parent.id() : WorldRepository.ROOT_ID;
        world.childParent.put(child.id(), newParent);
        world.parentChildren.putIfAbsent(newParent, new LinkedList<>());
        world.parentChildren.get(newParent).add(child.id());
        getLogger().warn("Entity {} linked to {}", child.id, parent == null ? null : parent.id);
    }
}
