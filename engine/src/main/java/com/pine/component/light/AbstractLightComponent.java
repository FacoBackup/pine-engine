package com.pine.component.light;

import com.pine.component.AbstractComponent;
import com.pine.component.Entity;
import com.pine.inspection.Color;
import com.pine.inspection.MutableField;
import com.pine.theme.Icons;
import com.pine.type.LightType;
import org.joml.Vector2f;

public abstract class AbstractLightComponent extends AbstractComponent {
    @MutableField(label = "Screen Space Shadows")
    public boolean sss = false;
    @MutableField(label = "Light inner cutoff distance")
    public float outerCutoff = 0.5f;
    @MutableField(label = "Color")
    public final Color color = new Color(1, 1, 1);
    @MutableField(label = "Attenuation")
    public final Vector2f attenuation = new Vector2f(1);
    public final LightType type = getLightType();
    @MutableField(label = "Light cutoff distance")
    public float innerCutoff = 50;

    public AbstractLightComponent(Entity entity) {
        super(entity);
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

