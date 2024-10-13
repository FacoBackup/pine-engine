package com.pine.component;

import com.pine.injection.PBean;
import com.pine.theme.Icons;

import java.util.LinkedList;
import java.util.Set;


public class RigidBodyComponent extends AbstractComponent {

    public RigidBodyComponent(Entity entity) {
        super(entity);
    }

    @Override
    public Set<Class<? extends AbstractComponent>> getDependencies() {
        return Set.of(PhysicsColliderComponent.class);
    }

    @Override
    public ComponentType getType() {
        return ComponentType.RIGID_BODY;
    }
}
