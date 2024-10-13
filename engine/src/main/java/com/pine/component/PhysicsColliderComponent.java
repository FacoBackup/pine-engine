package com.pine.component;

import com.pine.injection.PBean;
import com.pine.theme.Icons;

import java.util.LinkedList;
import java.util.Set;


public class PhysicsColliderComponent extends AbstractComponent {

    public PhysicsColliderComponent(Entity entity) {
        super(entity);
    }

    @Override
    public Set<Class<? extends AbstractComponent>> getDependencies() {
        return Set.of(RigidBodyComponent.class, MeshComponent.class);
    }

    @Override
    public ComponentType getType() {
        return ComponentType.PHYSICS_COLLIDER;
    }
}
