package com.pine.engine.core.service.world.request;

import com.pine.common.messages.Message;
import com.pine.common.messages.MessageSeverity;
import com.pine.engine.core.component.AbstractComponent;
import com.pine.engine.core.component.MetadataComponent;
import com.pine.engine.core.repository.WorldRepository;
import com.pine.engine.core.service.world.WorldService;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static com.pine.engine.core.repository.WorldRepository.ROOT_ID;

public class AddEntityWorldRequest extends AbstractWorldRequest {
    private final List<Class<? extends AbstractComponent>> components;
    private Integer parentId;
    private int entityId;

    public AddEntityWorldRequest(List<Class<? extends AbstractComponent>> components) {
        this(null, components);
    }

    public AddEntityWorldRequest(int parentId) {
        this(parentId, Collections.emptyList());
    }

    private AddEntityWorldRequest(Integer parentId, List<Class<? extends AbstractComponent>> components) {
        this.components = components;
        this.parentId = parentId;
    }

    @Override
    public Message run(WorldRepository repository, WorldService service) {
        entityId = repository.genNextId();

        HashMap<String, AbstractComponent> newComponents = new HashMap<>();
        repository.entities.put(entityId, newComponents);
        try {
            addComponent(MetadataComponent.class, newComponents, repository);
            for (Class<? extends AbstractComponent> component : components) {
                addComponent(component, newComponents, repository);
            }

            repository.parentChildren.put(entityId, new LinkedList<>());
            if (parentId == null || !repository.parentChildren.containsKey(parentId)) {
                parentId = ROOT_ID;
            }
            repository.childParent.put(entityId, parentId);
            repository.parentChildren.get(parentId).add(entityId);

            return new Message("Entity created successfully", MessageSeverity.SUCCESS);
        } catch (Exception e) {
            return new Message("Could add components to new entity", MessageSeverity.WARN);
        }
    }

    private void addComponent(Class<? extends AbstractComponent> clazz, HashMap<String, AbstractComponent> newComponents, WorldRepository repository) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (newComponents.containsKey(clazz.getSimpleName())) {
            return;
        }
        var instance = clazz.getConstructor(Integer.class).newInstance(entityId);
        if (repository.registerComponent(instance)) {
            Set<Class<? extends AbstractComponent>> dependencies = instance.getDependencies();
            for (Class<? extends AbstractComponent> dependency : dependencies) {
                addComponent(dependency, newComponents, repository);
            }
        }
    }
}
