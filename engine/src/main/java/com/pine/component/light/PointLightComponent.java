package com.pine.component.light;

import com.pine.PBean;
import com.pine.component.AbstractComponent;
import com.pine.component.EntityComponent;
import com.pine.component.TransformationComponent;
import com.pine.inspection.Color;
import com.pine.inspection.MutableField;
import com.pine.type.LightType;
import org.joml.Vector2f;

import java.util.Set;

@PBean
public class PointLightComponent extends AbstractLightComponent<PointLightComponent> {
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

    @Override
    LightType getType() {
        return LightType.POINT;
    }

    public PointLightComponent() {
    }

    public PointLightComponent(Integer entityId) {
        super(entityId);
    }

}

