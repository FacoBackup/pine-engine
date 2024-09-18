package com.pine.core.service.world.request;

import com.pine.core.repository.WorldRepository;
import com.pine.core.service.world.WorldService;

import javax.annotation.Nullable;
import java.util.LinkedList;

import static com.pine.core.repository.WorldRepository.ROOT_ID;

public class HierarchyWorldRequest extends AbstractWorldRequest {
    private Integer parent;
    private final int child;

    public HierarchyWorldRequest(@Nullable Integer parent, int child) {
        this.parent = parent;
        this.child = child;
    }

    @Override
    public RequestMessage run(WorldRepository repository, WorldService service) {
        if (!repository.entities.containsKey(child) || (parent != null && !repository.entities.containsKey(parent))) {
            return new RequestMessage("Entities were not found", true);
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

        return new RequestMessage("Entities linked successfully", false);
    }
}
