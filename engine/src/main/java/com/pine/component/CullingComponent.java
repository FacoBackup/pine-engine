package com.pine.component;

import com.pine.injection.EngineInjectable;

import java.util.Set;

@EngineInjectable
public class CullingComponent extends AbstractComponent {

    public long maxDistanceFromCamera = 300;

    public CullingComponent(Integer entityId) {
        super(entityId);
    }

    public CullingComponent() {
        super();
    }

    @Override
    protected Set<Class<? extends EntityComponent>> getDependenciesInternal() {
        return Set.of(TransformationComponent.class);
    }

    @Override
    public String getComponentName() {
        return "Culling";
    }
}
