package com.pine.component;

import com.pine.inspection.InspectableField;

import java.util.Set;

public class DecalComponent extends AbstractComponent {
    public DecalComponent(String entity) {
        super(entity);
    }

    @InspectableField(label = "Material")
    public String material;

    @Override
    public Set<ComponentType> getDependencies() {
        return Set.of(ComponentType.TRANSFORMATION);
    }

    @Override
    public ComponentType getType() {
        return ComponentType.DECAL;
    }
}
