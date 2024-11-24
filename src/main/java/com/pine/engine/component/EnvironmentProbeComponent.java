package com.pine.engine.component;

import java.util.Set;


public class EnvironmentProbeComponent extends AbstractComponent {
//    @InspectableField(label = "Max distance from probe")
//    public int maxDistanceFromProbe = 50;

    public EnvironmentProbeComponent(String entity) {
        super(entity);
    }

    @Override
    public Set<ComponentType> getDependencies() {
        return Set.of(ComponentType.TRANSFORMATION);
    }

    @Override
    public ComponentType getType() {
        return ComponentType.ENVIRONMENT_PROBE;
    }
}
