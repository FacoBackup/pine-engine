package com.pine.engine.core.service.world.request;

import com.pine.common.messages.Message;
import com.pine.common.messages.MessageSeverity;
import com.pine.engine.core.component.AbstractComponent;
import com.pine.engine.core.repository.WorldRepository;
import com.pine.engine.core.service.world.WorldService;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.pine.engine.core.repository.WorldRepository.ROOT_ID;

public class HierarchyWorldRequest extends AbstractWorldRequest {
    private Integer parent;
    private final int child;

    public HierarchyWorldRequest(@Nullable Integer parent, int child) {
        this.parent = parent;
        this.child = child;
    }

    @Override
    public Message run(WorldRepository repository, WorldService service) {
        if (!repository.entities.containsKey(child) || (parent != null && !repository.entities.containsKey(parent))) {
            return new Message("Entities were not found", MessageSeverity.ERROR);
        }
        if (parent == null) {
            parent = ROOT_ID;
        }
        Integer currentParent = repository.childParent.get(child);
        if (currentParent != null) {
            repository.parentChildren.get(currentParent).remove(child);
        }

        LinkedList<Integer> parentList = repository.parentChildren.getOrDefault(parent, new LinkedList<>());
        parentList.add(child);
        repository.parentChildren.put(parent, parentList);
        repository.childParent.put(child, parent);

        return new Message("Entities linked successfully", MessageSeverity.SUCCESS);
    }
}
