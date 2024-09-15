package com.pine.engine.core.component;

import com.pine.engine.core.EngineInjectable;

import java.util.Set;

@EngineInjectable
public class RigidBodyComponent extends AbstractComponent<RigidBodyComponent> {
    public RigidBodyComponent(Integer entityId) {
        super(entityId);
    }

    public RigidBodyComponent() {
        super();
    }

    @Override
    protected Set<Class<? extends AbstractComponent>> getDependenciesInternal() {
        return Set.of(TransformationComponent.class, PhysicsColliderComponent.class);
    }
}
