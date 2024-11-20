package com.pine.component;

import com.pine.inspection.InspectableField;
import com.pine.inspection.ResourceTypeField;
import com.pine.repository.streaming.StreamableResourceType;

import java.util.Set;


public class DecalComponent extends AbstractComponent {
    @ResourceTypeField(type = StreamableResourceType.MATERIAL)
    @InspectableField(label = "Material")
    public String material;

    public DecalComponent(String entity) {
        super(entity);
    }

    @Override
    public Set<ComponentType> getDependencies() {
        return Set.of(ComponentType.TRANSFORMATION, ComponentType.CULLING);
    }

    @Override
    public ComponentType getType() {
        return ComponentType.DECAL;
    }
}
