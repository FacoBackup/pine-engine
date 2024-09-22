package com.pine.tasks;

import com.pine.AbstractTree;
import com.pine.Loggable;
import com.pine.PBean;
import com.pine.PInject;
import com.pine.component.EntityComponent;
import com.pine.component.MetadataComponent;
import com.pine.repository.WorldRepository;
import com.pine.service.MessageService;
import com.pine.service.world.WorldHierarchyTree;
import com.pine.service.world.WorldService;
import com.pine.service.world.request.AbstractRequest;
import com.pine.service.world.request.RequestMessage;

import java.util.LinkedList;
import java.util.Vector;

import static com.pine.repository.WorldRepository.ROOT_ID;

@PBean
public class RequestProcessingTask extends AbstractTask implements Loggable {
    @PInject
    public WorldRepository worldRepository;

    @PInject
    public WorldService world;

    @PInject
    public MessageService messageService;

    private final Vector<AbstractRequest> requests = new Vector<>();

    @Override
    protected void tickInternal() {
        try {
            if (requests.isEmpty()) {
                return;
            }
            if (worldRepository.entities.isEmpty()) {
                worldRepository.initialize();
            }
            for (var request : requests) {
                RequestMessage message = request.run(worldRepository, world);
                if (message != null) {
                    messageService.onMessage(message.message(), message.isError());
                }
            }
            requests.clear();
            worldRepository.worldTree.branches.clear();

            for (var childId : worldRepository.parentChildren.get(ROOT_ID)) {
                updateTree(childId, worldRepository.worldTree.branches);
            }
        } catch (Exception e) {
            getLogger().error(e.getMessage(), e);
        }
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

    public void addRequest(AbstractRequest request) {
        requests.add(request);
    }
}
