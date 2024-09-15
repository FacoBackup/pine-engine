package com.pine.engine.core.component;

import com.pine.engine.core.EngineInjectable;

import java.util.List;
import java.util.Set;

@EngineInjectable
public class TerrainComponent extends AbstractComponent<TerrainComponent> {
    public boolean castsShadows = true;
    public String heightMapTextureId;
    public float heightScale = 1;

    public TerrainComponent(Integer entityId) {
        super(entityId);
    }

    public TerrainComponent() {
        super();
    }

    @Override
    protected Set<Class<? extends AbstractComponent>> getDependenciesInternal() {
        return Set.of(TransformationComponent.class);
    }
}
