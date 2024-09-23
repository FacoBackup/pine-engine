package com.pine.tools.repository;

import com.pine.AbstractTree;
import com.pine.PBean;
import com.pine.PInject;
import com.pine.component.EntityComponent;
import com.pine.component.MetadataComponent;
import com.pine.repository.WorldRepository;
import com.pine.service.world.WorldHierarchyTree;
import com.pine.service.world.WorldService;
import com.pine.tasks.AbstractTask;

import java.util.LinkedList;
import java.util.Vector;

import static com.pine.repository.WorldRepository.ROOT;
import static com.pine.repository.WorldRepository.ROOT_ID;

@PBean
public class WorldTreeTask extends AbstractTask {
    private final WorldHierarchyTree worldTree = new WorldHierarchyTree(ROOT);
    private int internalWorldChangeId;

    @PInject
    public WorldRepository worldRepository;

    @PInject
    public WorldService world;

    @Override
    protected void tickInternal() {
        if(worldRepository.getWorldChangeId() != internalWorldChangeId) {
            internalWorldChangeId = worldRepository.getWorldChangeId();
            worldTree.branches.clear();

            for (var childId : worldRepository.parentChildren.get(ROOT_ID)) {
                updateTree(childId, worldTree.branches);
            }
        }
    }

    public WorldHierarchyTree getHierarchyTree() {
        return worldTree;
    }

    private void updateTree(Integer entityId, Vector<AbstractTree<MetadataComponent, EntityComponent>> branch) {
        WorldHierarchyTree current = new WorldHierarchyTree(world.getComponent(entityId, MetadataComponent.class));
        branch.add(current);
        current.extraData.addAll(worldRepository.entities.get(entityId).values());
        LinkedList<Integer> children = worldRepository.parentChildren.get(entityId);
        if (children != null) {
            for (Integer childId : children) {
                updateTree(childId, current.branches);
            }
        }
    }
}
