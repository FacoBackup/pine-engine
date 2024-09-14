package com.pine.engine.core.component;

import java.util.List;

public class TerrainComponent extends AbstractComponent {
    public boolean castsShadows = true;
    public String terrainID;
    public float heightScale = 1;

    @Override
    public List<Class<? extends AbstractComponent>> getDependencies() {
        return List.of(TransformationComponent.class);
    }
}
