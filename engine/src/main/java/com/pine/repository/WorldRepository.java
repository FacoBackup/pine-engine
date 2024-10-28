package com.pine.repository;

import com.pine.Mutable;
import com.pine.SerializableRepository;
import com.pine.component.AbstractComponent;
import com.pine.component.ComponentType;
import com.pine.component.Entity;
import com.pine.component.TransformationComponent;
import com.pine.injection.PBean;

import java.util.*;

@PBean
public class WorldRepository implements Mutable, SerializableRepository {
    public static final String ROOT_ID = Entity.class.getCanonicalName();
    public final Entity rootEntity = new Entity(ROOT_ID, "World");
    public final Map<String, Entity> entityMap = new HashMap<>() {{
        put(rootEntity.id(), rootEntity);
    }};
    public final Map<String, LinkedList<String>> parentChildren = new HashMap<>() {{
        put(rootEntity.id(), new LinkedList<>());
    }};
    public final Map<String, String> childParent = new HashMap<>();
    public final Map<ComponentType, Map<String, AbstractComponent>> components = new HashMap<>() {{
        for (var type : ComponentType.values()) {
            put(type, new HashMap<>());
        }
    }};
    public final Map<String, Boolean> hiddenEntityMap = new HashMap<>();
    public final List<AbstractComponent> withChangedData = new ArrayList<>();

    private int changes = 0;
    private int frozenVersion = -1;

    public void registerComponent(AbstractComponent component) {
        components.get(component.getType()).put(component.getEntityId(), component);
    }

    public void unregisterComponents(String entity) {
        for (var c : components.values()) {
            c.remove(entity);
        }
    }

    @Override
    public int getChangeId() {
        return changes;
    }

    @Override
    public void registerChange() {
        changes = (int) (Math.random() * 10000);
    }

    @Override
    public boolean isNotFrozen() {
        return frozenVersion != getChangeId();
    }

    @Override
    public void freezeVersion() {
        frozenVersion = getChangeId();
    }

    public TransformationComponent getTransformationComponent(String id) {
        return (TransformationComponent) components.get(ComponentType.TRANSFORMATION).get(id);
    }
}