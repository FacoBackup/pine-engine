package com.pine.component.light;

import com.pine.PBean;
import com.pine.inspection.MutableField;
import com.pine.type.LightType;
import org.joml.Vector2f;

@PBean
public class DirectionalLightComponent extends AbstractLightComponent<DirectionalLightComponent> {

    @MutableField(label="Use Shadow map")
    public boolean shadowMap = true;
    @MutableField(label="Shadow Bias")
    public float shadowBias = 0.0001f;
    @MutableField(label = "Shadow ZNear")
    public float zNear = 1;
    @MutableField(label = "Shadow ZFar")
    public float zFar = 10000;
    @MutableField(label = "Shadow attenuation distance")
    public float shadowAttenuationMinDistance = 50;
    @MutableField(label = "Light Size")
    public float size = 35;
    public transient Vector2f atlasFace = new Vector2f();

    @Override
    LightType getType() {
        return LightType.DIRECTIONAL;
    }

    public DirectionalLightComponent() {
    }

    public DirectionalLightComponent(Integer entityId) {
        super(entityId);
    }

}

