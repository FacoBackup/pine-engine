package com.pine.component.light;

import com.pine.component.ComponentType;
import com.pine.inspection.InspectableField;
import com.pine.type.LightType;
import org.joml.Vector2f;


public class DirectionalLightComponent extends AbstractLightComponent {

    @InspectableField(label = "Use Shadow map")
    public boolean shadowMap = true;
    @InspectableField(label = "Shadow Bias")
    public float shadowBias = 0.0001f;
    @InspectableField(label = "Shadow ZNear")
    public float zNear = 1;
    @InspectableField(label = "Shadow ZFar")
    public float zFar = 10000;
    @InspectableField(label = "Shadow attenuation distance")
    public float shadowAttenuationMinDistance = 50;
    @InspectableField(label = "Light Size")
    public float size = 35;
    public Vector2f atlasFace = new Vector2f();

    public DirectionalLightComponent(String entity) {
        super(entity);
    }

    @Override
    LightType getLightType() {
        return LightType.DIRECTIONAL;
    }

    @Override
    public ComponentType getType() {
        return ComponentType.DIRECTIONAL_LIGHT;
    }
}

