package com.pine.component.light;

import com.pine.component.ComponentType;
import com.pine.component.Entity;
import com.pine.injection.PBean;
import com.pine.inspection.MutableField;
import com.pine.type.LightType;

import java.util.LinkedList;

public class PointLightComponent extends AbstractLightComponent {
    @MutableField(label="Shadow map")
    public boolean shadowMap = true;
    @MutableField(label="Shadow map Bias")
    public float shadowBias = 0.0001f;
    @MutableField(label = "Shadow map attenuation min distance")
    public float shadowAttenuationMinDistance = 50;
    @MutableField(label = "Shadow ZNear")
    public float zNear = 1;
    @MutableField(label = "Shadow ZFar")
    public float zFar = 10000;

    public PointLightComponent(Entity entity) {
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

