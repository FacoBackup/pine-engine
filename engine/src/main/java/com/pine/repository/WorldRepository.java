package com.pine.repository;

import com.pine.SerializableRepository;
import com.pine.component.Entity;
import com.pine.component.EntityComponent;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.injection.PInjector;

import java.util.List;

@PBean
public class WorldRepository implements Mutable, SerializableRepository {
    public static final String ROOT_ID = Entity.class.getCanonicalName();
    public final Entity rootEntity = new Entity(ROOT_ID, "World");
    transient private int worldChangeId = 0;

    @PInject
    transient public PInjector injector;

    @PInject
    public List<EntityComponent> allComponents;

    @Override
    public int getChangeId() {
        return worldChangeId;
    }

    @Override
    public void registerChange() {
        worldChangeId++;
    }
}
