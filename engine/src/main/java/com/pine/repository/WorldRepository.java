package com.pine.repository;

import com.pine.Mutable;
import com.pine.SerializableRepository;
import com.pine.component.AbstractComponent;
import com.pine.component.ComponentType;
import com.pine.component.Entity;
import com.pine.injection.PBean;
import com.pine.injection.PostCreation;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.*;

@PBean
public class WorldRepository implements Mutable, SerializableRepository {
    public static final String ROOT_ID = Entity.class.getCanonicalName();
    public final Entity rootEntity = new Entity(ROOT_ID, "World");
    public final Map<ComponentType, List<AbstractComponent>> components = new HashMap<>();
    private int changes = 0;
    private int frozenVersion = -1;

    @PostCreation
    public void onInitialize() {
        for (var type : ComponentType.values()) {
            components.put(type, new ArrayList<>());
        }
    }

    public void registerComponent(AbstractComponent component) {
        components.get(component.getType()).add(component);
    }

    public <T extends AbstractComponent> List<T> getComponentBag(ComponentType type) {
        return (List<T>) components.get(type);
    }

    public void unregisterComponents(Entity entity) {
        for (AbstractComponent c : entity.components.values()) {
            components.get(c.getType()).remove(c);
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
}