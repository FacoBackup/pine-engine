package com.pine.component;

import com.pine.inspection.InspectableField;


public class EnvironmentProbeComponent extends AbstractComponent {
    @InspectableField(label = "Max distance from probe")
    public int maxDistanceFromProbe = 50;

    public EnvironmentProbeComponent(Entity entity) {
        super(entity);
    }

    @Override
    public ComponentType getType() {
        return ComponentType.ENVIRONMENT_PROBE;
    }
}
