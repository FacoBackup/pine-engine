package com.pine.component.light;

import com.pine.component.AbstractComponent;
import com.pine.component.ComponentType;
import com.pine.inspection.Color;
import com.pine.inspection.InspectableField;
import com.pine.theme.Icons;
import com.pine.type.LightType;
import org.joml.Vector2f;

import java.util.Set;

public abstract class AbstractLightComponent extends AbstractComponent {
    @InspectableField(label = "Screen Space Shadows")
    public boolean sss = false;
    @InspectableField(label = "Inner cutoff distance", min = 0)
    public float innerCutoff = .5f;
    @InspectableField(label = "Outer cutoff distance", min = 1)
    public int outerCutoff = 100;
    @InspectableField(label = "Color")
    public final Color color = new Color(1, 1, 1);
    @InspectableField(label = "Intensity", min = 0)
    public float intensity = 1;
    @InspectableField(label = "Attenuation")
    public final Vector2f attenuation = new Vector2f(1);


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

    @Override
    public String getIcon() {
        return Icons.lightbulb;
    }
}

