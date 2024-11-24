package com.pine.engine.component.light;

import com.pine.engine.component.ComponentType;
import com.pine.common.inspection.InspectableField;
import com.pine.engine.type.LightType;


public class SpotLightComponent extends AbstractLightComponent {
    @InspectableField(label = "Impact radius")
    public float radius = 45;

    public SpotLightComponent(String entity) {
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

