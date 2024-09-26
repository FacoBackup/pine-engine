package com.pine.component.light;

import com.pine.PBean;
import com.pine.inspection.MutableField;
import com.pine.type.LightType;

@PBean
public class SphereLightComponent extends AbstractLightComponent<SphereLightComponent> {
    @MutableField(label = "Area light Radius")
    public float areaRadius = 1;

    @Override
    LightType getType() {
        return LightType.SPHERE;
    }

    public SphereLightComponent() {
    }

    public SphereLightComponent(Integer entityId) {
        super(entityId);
    }

}

