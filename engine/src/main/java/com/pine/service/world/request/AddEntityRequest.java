package com.pine.service.world.request;

import com.pine.Loggable;
import com.pine.component.EntityComponent;
import com.pine.component.MetadataComponent;
import com.pine.repository.WorldRepository;
import com.pine.service.world.WorldService;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.pine.repository.WorldRepository.ROOT_ID;

public class AddEntityRequest extends AbstractRequest implements Loggable {
    private final List<Class<? extends EntityComponent>> components;
    private Integer parentId;
    private int entityId;

    public AddEntityRequest(List<Class<? extends EntityComponent>> components) {
        this(null, components);
    }

    public AddEntityRequest(int parentId) {
        this(parentId, Collections.emptyList());
    }

    private AddEntityRequest(Integer parentId, List<Class<? extends EntityComponent>> components) {
        this.components = components;
        this.parentId = parentId;
    }

    @Override
    public RequestMessage run(WorldRepository repository, WorldService service) {
        entityId = repository.genNextId();

        ConcurrentHashMap<String, EntityComponent> newComponents = new ConcurrentHashMap<>();
        repository.entities.put(entityId, newComponents);
        try {
            addComponent(MetadataComponent.class, newComponents, repository);
            for (Class<? extends EntityComponent> component : components) {
                addComponent(component, newComponents, repository);
            }

            createHierarchy(repository);

            return new RequestMessage("Entity created successfully", false);
        } catch (Exception e) {
            getLogger().error("Error while adding component", e);
            return new RequestMessage("Error while adding component", true);
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
