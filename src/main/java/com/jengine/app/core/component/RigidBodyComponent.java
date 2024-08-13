package com.jengine.app.core.component;

import java.util.List;

public class RigidBodyComponent extends AbstractComponent{
    @Override
    public List<Class<? extends AbstractComponent>> getDependencies() {
        return List.of(TransformationComponent.class, PhysicsColliderComponent.class);
    }

    // TODO
}
