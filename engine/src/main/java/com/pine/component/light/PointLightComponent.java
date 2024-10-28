package com.pine.component.light;

import com.pine.component.ComponentType;
import com.pine.inspection.InspectableField;
import com.pine.type.LightType;

public class PointLightComponent extends AbstractLightComponent {
    @InspectableField(label="Shadow map")
    public boolean shadowMap = true;
    @InspectableField(label="Shadow map Bias")
    public float shadowBias = 0.0001f;
    @InspectableField(label = "Shadow map attenuation min distance")
    public float shadowAttenuationMinDistance = 50;
    @InspectableField(label = "Shadow ZNear")
    public float zNear = 1;
    @InspectableField(label = "Shadow ZFar")
    public float zFar = 10000;

    public PointLightComponent(String entity) {
        super(entity);
    }

    @Override
    LightType getLightType() {
        return LightType.POINT;
    }

    @Override
    public ComponentType getType() {
        return ComponentType.POINT_LIGHT;
    }
}

