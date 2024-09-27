package com.pine.component;

import com.pine.PBean;
import com.pine.theme.Icons;

import java.util.Set;

@PBean
public class RigidBodyComponent extends AbstractComponent<RigidBodyComponent> {
    public RigidBodyComponent(Integer entityId) {
        super(entityId);
    }

    public RigidBodyComponent() {
        super();
    }

    @Override
    protected Set<Class<? extends EntityComponent>> getDependenciesInternal() {
        return Set.of(TransformationComponent.class, PhysicsColliderComponent.class);
    }

    @Override
    public String getTitle() {
        return "Rigid body";
    }

    @Override
    public String getIcon() {
        return Icons.sports_baseball;
    }
}
