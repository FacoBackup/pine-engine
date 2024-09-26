package com.pine.component.light;

import com.pine.PBean;
import com.pine.inspection.MutableField;
import com.pine.type.LightType;

@PBean
public class SpotLightComponent extends AbstractLightComponent<SpotLightComponent> {
    @MutableField(label = "Impact radius")
    public float radius = 45;

    @Override
    LightType getType() {
        return LightType.SPOT;
    }

    public SpotLightComponent() {
    }

    public SpotLightComponent(Integer entityId) {
        super(entityId);
    }

}

