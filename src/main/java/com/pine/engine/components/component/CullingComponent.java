package com.pine.engine.components.component;

import java.util.List;

public class CullingComponent extends AbstractComponent{
    @Override
    public List<Class<? extends AbstractComponent>> getDependencies() {
        return List.of(TransformationComponent.class);
    }
}
