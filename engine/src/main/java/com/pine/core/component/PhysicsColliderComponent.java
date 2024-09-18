package com.pine.core.component;

import com.pine.core.EngineInjectable;

import java.util.Set;

@EngineInjectable
public class PhysicsColliderComponent extends AbstractComponent {

    public PhysicsColliderComponent(Integer entityId) {
        super(entityId);
    }

    public PhysicsColliderComponent() {
        super();
    }

    @Override
    protected Set<Class<? extends EntityComponent>> getDependenciesInternal() {
        return Set.of(TransformationComponent.class, RigidBodyComponent.class, MeshComponent.class);
    }


    @Override
    public String getComponentName() {
        return "Collider";
    }
}
