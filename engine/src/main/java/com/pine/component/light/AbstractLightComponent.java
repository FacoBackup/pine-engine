package com.pine.component.light;

import com.pine.component.AbstractComponent;
import com.pine.component.EntityComponent;
import com.pine.component.TransformationComponent;
import com.pine.inspection.Color;
import com.pine.inspection.MutableField;
import com.pine.type.LightType;
import org.joml.Vector2f;

import java.util.Set;

public abstract class AbstractLightComponent<T extends EntityComponent> extends AbstractComponent<T> {
    @MutableField(label = "Screen Space Shadows")
    public boolean sss = false;

    @MutableField(label = "Light inner cutoff distance")
    public float outerCutoff = 0.5f;
    @MutableField(label = "Color")
    public final Color color = new Color();
    @MutableField(label = "Attenuation")
    public final Vector2f attenuation = new Vector2f();
    public final LightType type = getType();
    @MutableField(label = "Light cutoff distance")
    public float innerCutoff = 50;

    public AbstractLightComponent() {
    }

    public AbstractLightComponent(Integer entityId) {
        super(entityId);
    }

    @Override
    protected Set<Class<? extends EntityComponent>> getDependenciesInternal() {
        return Set.of(TransformationComponent.class);
    }

    abstract LightType getType();

    @Override
    final public String getComponentName() {
        return type.getLabel();
    }

}
