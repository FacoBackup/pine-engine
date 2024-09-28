package com.pine.service.request;

import com.pine.repository.Message;
import com.pine.repository.MessageSeverity;
import com.pine.repository.WorldRepository;
import com.pine.service.world.WorldService;

import javax.annotation.Nullable;
import java.util.LinkedList;

import static com.pine.repository.WorldRepository.ROOT_ID;

public class HierarchyRequest extends AbstractRequest {
    private Integer parent;
    private final int child;

    public HierarchyRequest(@Nullable Integer parent, int child) {
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
            LinkedList<Integer> previousParent = repository.parentChildren.get(currentParent);
            previousParent.remove((Integer) child);
        }

        LinkedList<Integer> parentList = repository.parentChildren.getOrDefault(parent, new LinkedList<>());
        parentList.add(child);
        repository.parentChildren.put(parent, parentList);
        repository.childParent.put(child, parent);

        return new Message("Entities linked successfully", MessageSeverity.SUCCESS);
    }
}
