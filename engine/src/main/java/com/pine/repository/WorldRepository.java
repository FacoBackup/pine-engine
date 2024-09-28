package com.pine.repository;

import com.pine.PBean;
import com.pine.PInject;
import com.pine.SerializableRepository;
import com.pine.component.EntityComponent;
import com.pine.component.MetadataComponent;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@PBean
public class WorldRepository implements ChangeRecord, SerializableRepository {
    public static final ConcurrentHashMap<String, EntityComponent> EMPTY_MAP = new ConcurrentHashMap<>();
    public static final int ROOT_ID = 0;
    public static final MetadataComponent ROOT = new MetadataComponent(ROOT_ID);
    public final Map<Integer, ConcurrentHashMap<String, EntityComponent>> entities = new ConcurrentHashMap<>();
    public final Map<Integer, Integer> childParent = new ConcurrentHashMap<>();
    public final Map<Integer, LinkedList<Integer>> parentChildren = new ConcurrentHashMap<>();
    public final Map<Integer, Boolean> activeEntities = new ConcurrentHashMap<>();
    private final LinkedList<Integer> freeIds = new LinkedList<>();
    private int nextId = ROOT_ID + 1;
    private int worldChangeId = 0;

    @PInject
    public List<EntityComponent> components;

    public void initialize() {
        ConcurrentHashMap<String, EntityComponent> rootComponents = new ConcurrentHashMap<>();
        rootComponents.put(MetadataComponent.class.getSimpleName(), ROOT);
        entities.put(ROOT_ID, rootComponents);
        parentChildren.put(ROOT_ID, new LinkedList<>());
        childParent.put(ROOT_ID, ROOT_ID);
        ROOT.name = "World";
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

    @Override
    public int getChangeId() {
        return worldChangeId;
    }

    @Override
    public void registerChange() {
        worldChangeId++;
    }
}
