package com.pine.engine.core.component;

import java.util.List;

public class CullingComponent extends AbstractComponent{
    @Override
    public List<Class<? extends AbstractComponent>> getDependencies() {
        return List.of(TransformationComponent.class);
    }
}
