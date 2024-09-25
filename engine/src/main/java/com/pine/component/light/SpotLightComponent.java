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
public class SpotLightComponent extends AbstractLightComponent<SpotLightComponent> {
    @MutableField(label = "Impact radius")
    public float radius = 45;

    @Override
    LightType getType() {
        return LightType.SPOT;
    }

    public SpotLightComponent() {
    }

    public SpotLightComponent(Integer entityId) {
        super(entityId);
    }

}

