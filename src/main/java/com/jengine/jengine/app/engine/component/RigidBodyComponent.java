package com.jengine.jengine.app.engine.component;
import com.artemis.Component;

import java.util.List;

public class RigidBodyComponent extends AbstractComponent{
    @Override
    public List<Class<? extends AbstractComponent>> getDependencies() {
        return List.of(TransformationComponent.class, PhysicsColliderComponent.class);
    }

    // TODO
}
