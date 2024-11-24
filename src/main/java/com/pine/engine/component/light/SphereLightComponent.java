package com.pine.engine.component.light;

import com.pine.common.inspection.InspectableField;
import com.pine.engine.component.ComponentType;
import com.pine.engine.type.LightType;


public class SphereLightComponent extends AbstractLightComponent {
    @InspectableField(label = "Area light Radius")
    public float areaRadius = 1;

    public SphereLightComponent(String entity) {
        super(entity);
    }

    @Override
    LightType getLightType() {
        return LightType.SPHERE;
    }

    @Override
    public ComponentType getType() {
        return ComponentType.SPHERE_LIGHT;
    }
}

