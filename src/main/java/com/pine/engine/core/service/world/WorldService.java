package com.pine.engine.core.service.world;

import com.pine.app.core.ui.view.AbstractTree;
import com.pine.common.messages.MessageCollector;
import com.pine.engine.core.EngineDependency;
import com.pine.engine.core.EngineInjectable;
import com.pine.engine.core.component.EntityComponent;
import com.pine.engine.core.component.MetadataComponent;
import com.pine.engine.core.component.TransformationComponent;
import com.pine.engine.core.repository.WorldRepository;
import com.pine.engine.core.service.AbstractMultithreadedService;
import com.pine.engine.core.service.world.request.AbstractWorldRequest;
import jakarta.annotation.Nullable;

import java.util.*;

import static com.pine.engine.core.repository.WorldRepository.ROOT_ID;

@EngineInjectable
public class WorldService extends AbstractMultithreadedService {
    private static final String TRANSFORMATION_CLASS = TransformationComponent.class.getSimpleName();

    @EngineDependency
    public WorldRepository worldRepository;

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

    private void updateTree(Integer entityId, Vector<AbstractTree<MetadataComponent, EntityComponent>> branch) {
        LinkedList<Integer> children = worldRepository.parentChildren.get(entityId);
        WorldHierarchyTree current = new WorldHierarchyTree(getComponent(entityId, MetadataComponent.class));
        branch.add(current);
        current.extraData.addAll(worldRepository.entities.get(entityId).values());
        if (children != null) {
            for (Integer childId : children) {
                updateTree(childId, current.branches);
            }
        }
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends EntityComponent> T getComponent(Integer entityId, Class<T> component) {
        var components = worldRepository.entities.get(entityId);
        if (components != null) {
            return (T) components.get(component.getSimpleName());
        }
        return null;
    }

    public WorldHierarchyTree getHierarchyTree() {
        return worldRepository.worldTree;
    }

    public void addRequest(AbstractWorldRequest request) {
        requests.add(request);
    }

    public Object getTransformationComponentUnchecked(int entityId) {
        return worldRepository.entities.get(entityId).get(TRANSFORMATION_CLASS);
    }
}
