package com.pine.engine.component;

import com.pine.common.inspection.InspectableField;
import com.pine.engine.inspection.ResourceTypeField;
import com.pine.engine.repository.streaming.StreamableResourceType;

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
