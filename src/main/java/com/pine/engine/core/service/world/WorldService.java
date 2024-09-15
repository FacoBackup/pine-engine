package com.pine.engine.core.service.world;

import com.pine.app.core.ui.view.AbstractTree;
import com.pine.common.messages.MessageCollector;
import com.pine.engine.core.EngineInjectable;
import com.pine.engine.core.component.AbstractComponent;
import com.pine.engine.core.component.MetadataComponent;
import com.pine.engine.core.repository.WorldRepository;
import com.pine.engine.core.service.AbstractMultithreadedService;
import com.pine.engine.core.service.world.request.AbstractWorldRequest;
import jakarta.annotation.Nullable;

import java.util.*;

import static com.pine.engine.core.repository.WorldRepository.ROOT_ID;

@EngineInjectable
public class WorldService extends AbstractMultithreadedService {
    @EngineInjectable
    private final WorldRepository worldRepository = new WorldRepository();

    private final List<AbstractWorldRequest> requests = new ArrayList<>();

    @Override
    protected void tickInternal() {
        if (requests.isEmpty()) {
            return;
        }
        if (worldRepository.entities.isEmpty()) {
            worldRepository.initialize();
        }
        for (var request : requests) {
            MessageCollector.pushMessage(request.run(worldRepository, this));
        }
        requests.clear();
        worldRepository.worldTree.branches.clear();

        for (var childId : worldRepository.parentChildren.get(ROOT_ID)) {
            updateTree(childId, worldRepository.worldTree.branches);
        }
    }

    private void updateTree(Integer entityId, Vector<AbstractTree<MetadataComponent>> branch) {
        LinkedList<Integer> children = worldRepository.parentChildren.get(entityId);
        Tree current = new Tree(getComponent(entityId, MetadataComponent.class));
        branch.add(current);
        if (children != null) {
            for (Integer childId : children) {
                updateTree(childId, current.branches);
            }
        }
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends AbstractComponent> T getComponent(Integer entityId, Class<T> component) {
        HashMap<String, AbstractComponent> components = worldRepository.entities.get(entityId);
        if (components != null) {
            return (T) components.get(component.getSimpleName());
        }
        return null;
    }

    public Tree getHierarchyTree() {
        return worldRepository.worldTree;
    }

    public void addRequest(AbstractWorldRequest request) {
        requests.add(request);
    }
}
