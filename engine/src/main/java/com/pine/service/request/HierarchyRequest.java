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
        for (var tile : hashGridService.getLoadedTiles()) {
            if (tile != null && tile.getWorld().entityMap.containsKey(child.id()) && (parent == null || tile.getWorld().entityMap.containsKey(parent.id()))) {
                String previousParent = tile.getWorld().childParent.get(child.id());
                tile.getWorld().parentChildren.get(previousParent).remove(child.id());
                String newParent = parent != null ? parent.id() : tile.getWorld().rootEntity.id();
                tile.getWorld().childParent.put(child.id(), newParent);
                tile.getWorld().parentChildren.putIfAbsent(newParent, new LinkedList<>());
                tile.getWorld().parentChildren.get(newParent).add(child.id());
                getLogger().warn("Entity {} linked to {}", child.id, parent == null ? null : parent.id);
            }
        }
    }
}
