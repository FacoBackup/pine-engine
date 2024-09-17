package com.pine.engine.core.service.world.request;

import com.pine.common.Loggable;
import com.pine.common.messages.Message;
import com.pine.common.messages.MessageSeverity;
import com.pine.engine.core.component.AbstractComponent;
import com.pine.engine.core.component.EntityComponent;
import com.pine.engine.core.component.MetadataComponent;
import com.pine.engine.core.repository.WorldRepository;
import com.pine.engine.core.service.world.WorldService;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.pine.engine.core.repository.WorldRepository.ROOT_ID;

public class AddEntityWorldRequest extends AbstractWorldRequest implements Loggable {
    private final List<Class<? extends EntityComponent>> components;
    private Integer parentId;
    private int entityId;

    public AddEntityWorldRequest(List<Class<? extends EntityComponent>> components) {
        this(null, components);
    }

    public AddEntityWorldRequest(int parentId) {
        this(parentId, Collections.emptyList());
    }

    private AddEntityWorldRequest(Integer parentId, List<Class<? extends EntityComponent>> components) {
        this.components = components;
        this.parentId = parentId;
    }

    @Override
    public Message run(WorldRepository repository, WorldService service) {
        entityId = repository.genNextId();

        ConcurrentHashMap<String, EntityComponent> newComponents = new ConcurrentHashMap<>();
        repository.entities.put(entityId, newComponents);
        try {
            addComponent(MetadataComponent.class, newComponents, repository);
            for (Class<? extends EntityComponent> component : components) {
                addComponent(component, newComponents, repository);
            }

            createHierarchy(repository);

            return new Message("Entity created successfully", MessageSeverity.SUCCESS);
        } catch (Exception e) {
            getLogger().error("Error while adding component", e);
            return new Message("Error while adding component", MessageSeverity.ERROR);
        }
    }

    private void createHierarchy(WorldRepository repository) {
        repository.parentChildren.put(entityId, new LinkedList<>());
        if (parentId == null || !repository.parentChildren.containsKey(parentId)) {
            parentId = ROOT_ID;
        }
        repository.childParent.put(entityId, parentId);
        repository.parentChildren.get(parentId).add(entityId);
    }

    private void addComponent(Class<? extends EntityComponent> clazz, Map<String, EntityComponent> newComponents, WorldRepository repository) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (newComponents.containsKey(clazz.getSimpleName())) {
            return;
        }
        var instance = clazz.getConstructor(Integer.class).newInstance(entityId);
        if (repository.registerComponent(instance)) {
            Set<Class<? extends EntityComponent>> dependencies = instance.getDependencies();
            for (var dependency : dependencies) {
                addComponent(dependency, newComponents, repository);
            }
        }
    }
}
