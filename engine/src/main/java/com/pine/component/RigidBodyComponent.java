package com.pine.component;

import java.util.Set;


public class RigidBodyComponent extends AbstractComponent {

    public RigidBodyComponent(Entity entity) {
        super(entity);
    }

    @Override
    public Set<ComponentType> getDependencies() {
        return Set.of(ComponentType.PHYSICS_COLLIDER);
    }

    @Override
    public ComponentType getType() {
        return ComponentType.RIGID_BODY;
    }
}
