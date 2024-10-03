package com.pine.component;

import com.pine.injection.PBean;
import com.pine.theme.Icons;

import java.util.LinkedList;
import java.util.Set;

@PBean
public class PhysicsColliderComponent extends AbstractComponent<PhysicsColliderComponent> {

    public PhysicsColliderComponent(Entity entity, LinkedList<?> bag) {
        super(entity, bag);
    }

    public PhysicsColliderComponent() {}

    @Override
    public Set<Class<? extends EntityComponent>> getDependencies() {
        return Set.of(RigidBodyComponent.class, PrimitiveComponent.class);
    }

    @Override
    public String getTitle() {
        return "Collider";
    }

    @Override
    public String getIcon() {
        return Icons.sports_tennis;
    }
}
