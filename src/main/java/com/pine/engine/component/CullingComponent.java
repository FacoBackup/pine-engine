package com.pine.engine.component;

import com.pine.common.inspection.InspectableField;

public class CullingComponent extends AbstractComponent {
    @InspectableField(label = "Enable culling")
    public boolean isCullingEnabled = true;
    @InspectableField(label = "Max distance from camera", min = 1)
    public int maxDistanceFromCamera = 200;
    @InspectableField(label = "Sphere radius")
    public final float cullingSphereRadius = 1;

    @InspectableField(label = "Distance from camera", disabled = true)
    public float distanceFromCamera = 0f;

    public CullingComponent(String entityId) {
        super(entityId);
    }

    @Override
    public ComponentType getType() {
        return ComponentType.CULLING;
    }
}
