package com.pine.component.light;

import com.pine.component.ComponentType;
import com.pine.component.Entity;
import com.pine.inspection.MutableField;
import com.pine.type.LightType;


public class SpotLightComponent extends AbstractLightComponent {
    @MutableField(label = "Impact radius")
    public float radius = 45;

    public SpotLightComponent(Entity entity) {
        super(entity);
    }

    @Override
    LightType getLightType() {
        return LightType.SPOT;
    }

    @Override
    public ComponentType getType() {
        return ComponentType.SPOT_LIGHT;
    }
}

