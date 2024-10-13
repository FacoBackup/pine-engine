package com.pine.component.light;

import com.pine.component.ComponentType;
import com.pine.component.Entity;
import com.pine.injection.PBean;
import com.pine.inspection.MutableField;
import com.pine.type.LightType;
import org.joml.Vector2f;

import java.util.LinkedList;


public class DirectionalLightComponent extends AbstractLightComponent {

    @MutableField(label = "Use Shadow map")
    public boolean shadowMap = true;
    @MutableField(label = "Shadow Bias")
    public float shadowBias = 0.0001f;
    @MutableField(label = "Shadow ZNear")
    public float zNear = 1;
    @MutableField(label = "Shadow ZFar")
    public float zFar = 10000;
    @MutableField(label = "Shadow attenuation distance")
    public float shadowAttenuationMinDistance = 50;
    @MutableField(label = "Light Size")
    public float size = 35;
    public Vector2f atlasFace = new Vector2f();

    public DirectionalLightComponent(Entity entity) {
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

