package com.pine.component.light;

import com.pine.PBean;
import com.pine.component.Entity;
import com.pine.inspection.MutableField;
import com.pine.type.LightType;

import java.util.LinkedList;

@PBean
public class SphereLightComponent extends AbstractLightComponent<SphereLightComponent> {
    @MutableField(label = "Area light Radius")
    public float areaRadius = 1;

    public SphereLightComponent(Entity entity, LinkedList<?> bag) {
        super(entity, bag);
    }

    public SphereLightComponent() {}

    @Override
    LightType getType() {
        return LightType.SPHERE;
    }

}

