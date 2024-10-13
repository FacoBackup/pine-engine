package com.pine.component.light;

import com.pine.component.ComponentType;
import com.pine.component.Entity;
import com.pine.injection.PBean;
import com.pine.inspection.MutableField;
import com.pine.type.LightType;

import java.util.LinkedList;


public class SphereLightComponent extends AbstractLightComponent {
    @MutableField(label = "Area light Radius")
    public float areaRadius = 1;

    public SphereLightComponent(Entity entity) {
        super(entity);
    }

    @Override
    LightType getLightType() {
        return LightType.SPHERE;
    }

    @Override
    public ComponentType getType() {
        return ComponentType.SPHERE_LIGHT;
    }
}

