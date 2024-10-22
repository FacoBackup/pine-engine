package com.pine.component.light;

import com.pine.component.ComponentType;
import com.pine.component.Entity;
import com.pine.inspection.InspectableField;
import com.pine.type.LightType;


public class SphereLightComponent extends AbstractLightComponent {
    @InspectableField(label = "Area light Radius")
    public float areaRadius = 1;

    public SphereLightComponent(Entity entity) {
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

