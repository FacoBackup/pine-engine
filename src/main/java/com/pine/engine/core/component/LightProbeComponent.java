package com.pine.engine.core.component;

import java.util.List;

public class LightProbeComponent extends AbstractComponent {
    public int mipmaps = 6;
    public int maxDistance = 50;

    @Override
    public List<Class<? extends AbstractComponent>> getDependencies() {
        return List.of(TransformationComponent.class);
    }
}
