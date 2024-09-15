package com.pine.engine.core.component;

import com.pine.engine.core.EngineInjectable;

import java.util.List;
import java.util.Set;

@EngineInjectable
public class PhysicsColliderComponent extends AbstractComponent<PhysicsColliderComponent> {

    public PhysicsColliderComponent(Integer entityId) {
        super(entityId);
    }

    public PhysicsColliderComponent() {
        super();
    }

    @Override
    protected Set<Class<? extends AbstractComponent>> getDependenciesInternal() {
        return Set.of(TransformationComponent.class, RigidBodyComponent.class, MeshComponent.class);
    }
}
