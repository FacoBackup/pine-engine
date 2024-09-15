package com.pine.engine.core.component;

import com.pine.engine.core.EngineInjectable;

import java.util.Set;

@EngineInjectable
public class CullingComponent extends AbstractComponent<CullingComponent> {

    public long maxDistanceFromCamera = 300;

    public CullingComponent(Integer entityId) {
        super(entityId);
    }

    public CullingComponent() {
        super();
    }

    @Override
    protected Set<Class<? extends AbstractComponent>> getDependenciesInternal() {
        return Set.of(TransformationComponent.class);
    }
}
