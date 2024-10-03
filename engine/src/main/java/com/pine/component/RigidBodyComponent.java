package com.pine.component;

import com.pine.injection.PBean;
import com.pine.theme.Icons;

import java.util.LinkedList;
import java.util.Set;

@PBean
public class RigidBodyComponent extends AbstractComponent<RigidBodyComponent> {

    public RigidBodyComponent(Entity entity, LinkedList<?> bag) {
        super(entity, bag);
    }

    public RigidBodyComponent() {
    }

    @Override
    public Set<Class<? extends EntityComponent>> getDependencies() {
        return Set.of(PhysicsColliderComponent.class);
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
