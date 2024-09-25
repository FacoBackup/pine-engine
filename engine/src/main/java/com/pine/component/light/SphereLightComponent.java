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

