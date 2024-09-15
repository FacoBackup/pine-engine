package com.pine.engine.core.repository;

import com.google.gson.JsonElement;
import com.pine.common.Initializable;
import com.pine.engine.core.EngineDependency;
import com.pine.engine.core.EngineInjectable;
import com.pine.engine.core.component.AbstractComponent;
import com.pine.engine.core.component.MetadataComponent;
import com.pine.engine.core.service.serialization.SerializableRepository;
import com.pine.engine.core.service.world.Tree;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@EngineInjectable
public class WorldRepository extends SerializableRepository implements Initializable {
    public static final int ROOT_ID = 0;
    private static final MetadataComponent ROOT = new MetadataComponent(ROOT_ID);
    public final Map<Integer, HashMap<String, AbstractComponent>> entities = new HashMap<>();
    public final Map<Integer, Integer> childParent = new HashMap<>();
    public final Map<Integer, LinkedList<Integer>> parentChildren = new HashMap<>();
    public final Map<Integer, Boolean> activeEntities = new HashMap<>();
    public final Tree worldTree = new Tree(ROOT);
    private final LinkedList<Integer> freeIds = new LinkedList<>();
    private int nextId = ROOT_ID + 1;

    @EngineDependency
    public List<AbstractComponent> components;


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
        HashMap<String, AbstractComponent> rootComponents = new HashMap<>();
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

    public boolean registerComponent(AbstractComponent instance) {
        try {
            entities.get(instance.getEntityId()).put(instance.getClass().getSimpleName(), instance);
            components
                    .stream()
                    .filter(a -> instance.getClass().isAssignableFrom(a.getClass()))
                    .findFirst()
                    .ifPresent(a -> a.bag.add(instance));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
