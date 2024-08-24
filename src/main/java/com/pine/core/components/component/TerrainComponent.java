package com.pine.core.components.component;

import java.util.List;

public class TerrainComponent extends AbstractComponent{
    private boolean castsShadows = true;
    private String terrainID;
    private float heightScale= 1;

    @Override
    public List<Class<? extends AbstractComponent>> getDependencies() {
        return List.of(TransformationComponent.class);
    }

    public boolean isCastsShadows() {
        return castsShadows;
    }

    public void setCastsShadows(boolean castsShadows) {
        this.castsShadows = castsShadows;
    }

    public String getTerrainID() {
        return terrainID;
    }

    public void setTerrainID(String terrainID) {
        this.terrainID = terrainID;
    }

    public float getHeightScale() {
        return heightScale;
    }

    public void setHeightScale(float heightScale) {
        this.heightScale = heightScale;
    }
}
