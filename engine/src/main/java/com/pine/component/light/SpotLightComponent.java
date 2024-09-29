package com.pine.component.light;

import com.pine.PBean;
import com.pine.component.Entity;
import com.pine.inspection.MutableField;
import com.pine.type.LightType;
import java.util.LinkedList;

@PBean
public class SpotLightComponent extends AbstractLightComponent<SpotLightComponent> {
    @MutableField(label = "Impact radius")
    public float radius = 45;

    public SpotLightComponent(Entity entity, LinkedList<?> bag) {
        super(entity, bag);
    }

    public SpotLightComponent() {}

    @Override
    LightType getType() {
        return LightType.SPOT;
    }
}

