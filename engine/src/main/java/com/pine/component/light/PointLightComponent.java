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
public class PointLightComponent extends AbstractComponent<PointLightComponent> {
    @MutableField(label="Screen Space Shadows")
    public boolean screenSpaceShadows = false;
    @MutableField(label="Shadow map")
    public boolean shadowMap = true;
    @MutableField(label="Shadow map Bias")
    public float shadowBias = 0.0001f;
    @MutableField(label = "SSS samples")
    public int shadowSamples = 3;
    @MutableField(label = "Directional Light ZNear")
    public float zNear = 1;
    @MutableField(label = "Directional Light ZFar")
    public float zFar = 10000;
    @MutableField(label = "Shadow map cutoff")
    public float cutoff = 50;
    @MutableField(label = "Shadow map attenuation min distance")
    public float shadowAttenuationMinDistance = 50;
    @MutableField(label = "Attenuation")
    public final Vector2f attenuation = new Vector2f();
    @MutableField(label = "Fallout smoothing")
    public float smoothing = 0.5f;
    @MutableField(label = "Impact radius")
    public float radius = 45;
    @MutableField(label = "Directional light Size")
    public float size = 35;
    @MutableField(label = "Area light Radius")
    public float areaRadius = 1;
    @MutableField(label = "Area light Width")
    public float planeAreaWidth = 1;
    @MutableField(label = "Area light Height")
    public float planeAreaHeight = 1;
    @MutableField(label = "Intensity")
    public int intensity = 1;

    @MutableField(label = "Type")
    public LightType type = LightType.DIRECTIONAL;
    @MutableField(label = "Color")
    public final Color color = new Color();

    public final transient Vector2f atlasFace = new Vector2f();

    public PointLightComponent(Integer entityId) {
        super(entityId);
    }

    public PointLightComponent() {
        super();
    }

    @Override
    protected Set<Class<? extends EntityComponent>> getDependenciesInternal() {
        return Set.of(TransformationComponent.class);
    }

    @Override
    public String getComponentName() {
        return "Light";
    }
}

