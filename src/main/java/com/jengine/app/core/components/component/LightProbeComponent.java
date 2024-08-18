package com.jengine.app.core.components.component;

import java.util.List;

public class LightProbeComponent extends AbstractComponent{
    private int mipmaps = 6;
    private int maxDistance = 50;

    @Override
    public List<Class<? extends AbstractComponent>> getDependencies() {
        return List.of(TransformationComponent.class);
    }

    public int getMipmaps() {
        return mipmaps;
    }

    public void setMipmaps(int mipmaps) {
        this.mipmaps = mipmaps;
    }

    public int getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(int maxDistance) {
        this.maxDistance = maxDistance;
    }
}
