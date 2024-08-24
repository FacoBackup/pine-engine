package com.pine.core.components.component;

import java.util.List;

public class PhysicsColliderComponent extends AbstractComponent {
    @Override
    public List<Class<? extends AbstractComponent>> getDependencies() {
        return List.of(TransformationComponent.class, RigidBodyComponent.class, MeshComponent.class);
    }
}
