package com.pine.repository;

import com.google.gson.JsonElement;
import com.pine.Initializable;
import com.pine.injection.EngineDependency;
import com.pine.injection.EngineInjectable;
import com.pine.component.EntityComponent;
import com.pine.component.MetadataComponent;
import com.pine.service.serialization.SerializableRepository;
import com.pine.service.world.WorldHierarchyTree;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EngineInjectable
public class WorldRepository extends SerializableRepository implements Initializable {
    public static final int ROOT_ID = 0;
    private static final MetadataComponent ROOT = new MetadataComponent(ROOT_ID);
    public final Map<Integer, ConcurrentHashMap<String, EntityComponent>> entities = new ConcurrentHashMap<>();
    public final Map<Integer, Integer> childParent = new ConcurrentHashMap<>();
    public final Map<Integer, LinkedList<Integer>> parentChildren = new ConcurrentHashMap<>();
    public final Map<Integer, Boolean> activeEntities = new ConcurrentHashMap<>();
    public final WorldHierarchyTree worldTree = new WorldHierarchyTree(ROOT);
    private final LinkedList<Integer> freeIds = new LinkedList<>();
    private int nextId = ROOT_ID + 1;

    @EngineDependency
    public List<EntityComponent> components;


    @Override
    protected void parseInternal(JsonElement data) {
        // TODO
    }

    @Override
    public JsonElement serializeData() {
        // TODO
        return null;
    }

    public void initialize() {
        ConcurrentHashMap<String, EntityComponent> rootComponents = new ConcurrentHashMap<>();
        rootComponents.put(MetadataComponent.class.getSimpleName(), ROOT);
        entities.put(ROOT_ID, rootComponents);
        parentChildren.put(ROOT_ID, new LinkedList<>());
        childParent.put(ROOT_ID, ROOT_ID);

    }

    public int genNextId() {
        int entityId;
        if (!freeIds.isEmpty()) {
            entityId = freeIds.getLast();
            freeIds.remove(entityId);
        } else {
            entityId = nextId;
            nextId += 1;
        }
        return entityId;
    }

    @Override
    public void onInitialize() {
        ROOT.name = "World";
    }

    public boolean registerComponent(EntityComponent instance) {
        try {
            entities.get(instance.getEntityId()).put(instance.getClass().getSimpleName(), instance);
            components
                    .stream()
                    .filter(a -> instance.getClass().isAssignableFrom(a.getClass()))
                    .findFirst()
                    .ifPresent(a -> a.addComponent(instance));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
