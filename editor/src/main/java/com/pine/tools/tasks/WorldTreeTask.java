package com.pine.tools.tasks;

import com.pine.PBean;
import com.pine.PInject;
import com.pine.component.AbstractComponent;
import com.pine.component.EntityComponent;
import com.pine.component.MetadataComponent;
import com.pine.repository.WorldRepository;
import com.pine.service.world.WorldService;
import com.pine.tasks.AbstractTask;
import com.pine.theme.Icons;

import java.util.*;

import static com.pine.repository.WorldRepository.ROOT_ID;

@PBean
public class WorldTreeTask extends AbstractTask {
    private final HierarchyTree worldTree = new HierarchyTree(ROOT_ID, "World", Icons.inventory_2);
    private final Map<Integer, HierarchyTree> nodes = new HashMap<>();
    private int internalWorldChangeId;

    @PInject
    public WorldRepository worldRepository;

    @PInject
    public WorldService world;

    @Override
    protected void tickInternal() {
        if (worldRepository.getChangeId() != internalWorldChangeId) {
            update();
        }
    }

    public HierarchyTree getHierarchyTree() {
        return worldTree;
    }

    public void update() {
        nodes.clear();
        internalWorldChangeId = worldRepository.getChangeId();
        worldTree.children.clear();
        nodes.put(ROOT_ID, worldTree);
        for (var childId : worldRepository.parentChildren.get(ROOT_ID)) {
            updateTree(childId, worldTree.children);
        }
    }

    public Map<Integer, HierarchyTree> getNodes() {
        return nodes;
    }

    private void updateTree(int entityId, List<HierarchyTree> branch) {
        MetadataComponent metadata = world.getComponent(entityId, MetadataComponent.class);
        if (metadata == null) {
            return;
        }

        HierarchyTree current = new HierarchyTree(entityId, metadata.name, Icons.inventory_2, true, new ArrayList<>());
        nodes.put(current.id, current);
        branch.add(current);

        for (EntityComponent c : worldRepository.entities.get(entityId).values()) {
            AbstractComponent<?> component = (AbstractComponent<?>) c;
            if (component != metadata) {
                current.children.add(new HierarchyTree(c.getEntityId(), component.getTitle(), component.getIcon(), false, Collections.emptyList()));
            }
        }

        LinkedList<Integer> actualChildren = worldRepository.parentChildren.get(entityId);
        if (actualChildren != null) {
            for (Integer childId : actualChildren) {
                updateTree(childId, current.children);
            }
        }
    }
}
