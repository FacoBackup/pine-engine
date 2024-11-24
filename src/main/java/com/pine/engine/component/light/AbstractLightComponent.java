package com.pine.engine.component.light;

import com.pine.common.inspection.Color;
import com.pine.common.inspection.InspectableField;
import com.pine.engine.component.AbstractComponent;
import com.pine.engine.component.ComponentType;
import com.pine.engine.type.LightType;

import java.util.Set;

import static com.pine.engine.service.world.WorldGrid.TILE_SIZE;

public abstract class AbstractLightComponent extends AbstractComponent {
    @InspectableField(label = "Screen Space Shadows")
    public boolean sss = false;
    @InspectableField(label = "Inner cutoff distance", min = 0)
    public float innerCutoff = .5f;
    @InspectableField(label = "Outer cutoff distance", min = 1, max = TILE_SIZE)
    public int outerCutoff = TILE_SIZE;
    @InspectableField(label = "Color")
    public final Color color = new Color(1, 1, 1);
    @InspectableField(label = "Intensity", min = 0)
    public float intensity = 1;


    public final LightType type = getLightType();

    public AbstractLightComponent(String entity) {
        super(entity);
    }

    @Override
    public Set<ComponentType> getDependencies() {
        return Set.of(ComponentType.TRANSFORMATION);
    }

    abstract LightType getLightType();

    @Override
    final public String getTitle() {
        return type.getTitle();
    }
}

