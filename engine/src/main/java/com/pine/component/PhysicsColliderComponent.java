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
    public Set<ComponentType> getDependencies() {
        return Set.of(ComponentType.RIGID_BODY, ComponentType.MESH);
    }

    @Override
    public ComponentType getType() {
        return ComponentType.PHYSICS_COLLIDER;
    }
}
