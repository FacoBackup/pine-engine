package com.pine.component;

import com.pine.PBean;

import java.util.Set;

@PBean
public class PhysicsColliderComponent extends AbstractComponent<PhysicsColliderComponent> {

    public PhysicsColliderComponent(Integer entityId) {
        super(entityId);
    }

    public PhysicsColliderComponent() {
        super();
    }

    @Override
    protected Set<Class<? extends EntityComponent>> getDependenciesInternal() {
        return Set.of(TransformationComponent.class, RigidBodyComponent.class, SceneComponent.class);
    }


    @Override
    public String getComponentName() {
        return "Collider";
    }
}
