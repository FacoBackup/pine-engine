package com.pine.engine.core.component;

import com.pine.engine.core.EngineInjectable;
import org.joml.Vector3f;

import java.util.List;
import java.util.Set;

@EngineInjectable
public class MetadataComponent extends AbstractComponent<MetadataComponent> {
    public final Vector3f pickerId = new Vector3f();
    public String name = "New Entity";
    public final long creationDate = System.currentTimeMillis();

    public MetadataComponent(int entityId) {
        super(entityId);
        pickerId.x = (float) ((entityId) & 0xFF) / 0xFF;
        pickerId.y = (float) ((entityId >> 8) & 0xFF) / 0xFF;
        pickerId.z = (float) ((entityId >> 16) & 0xFF) / 0xFF;
    }

    public MetadataComponent() {
        super();
    }

    @Override
    protected Set<Class<? extends AbstractComponent>> getDependenciesInternal() {
        return Set.of();
    }
}
