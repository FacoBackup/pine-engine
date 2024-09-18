package com.pine.core.component;

import com.pine.core.EngineInjectable;

import java.util.Set;

@EngineInjectable
public class TerrainComponent extends AbstractComponent {
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
    protected Set<Class<? extends EntityComponent>> getDependenciesInternal() {
        return Set.of(TransformationComponent.class);
    }

    @Override
    public String getComponentName() {
        return "Terrain";
    }
}
