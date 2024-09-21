package com.pine.component;

import com.pine.injection.EngineInjectable;
import com.pine.inspection.Color;
import com.pine.inspection.MutableField;
import com.pine.type.LightType;
import org.joml.Vector2f;

import java.util.Set;

@EngineInjectable
public class LightComponent extends AbstractComponent<LightComponent> {
    // TODO - BREAK INTO SEPARATED COMPONENT TYPES
    @MutableField(label="Screen Space Shadows")
    public boolean screenSpaceShadows = false;
    @MutableField(label="Shadow map")
    public boolean shadowMap = true;
    @MutableField(label="Shadow map Bias")
    public double shadowBias = 0.0001;
    @MutableField(label = "SSS samples")
    public int shadowSamples = 3;
    @MutableField(label = "Directional Light ZNear")
    public double zNear = 1;
    @MutableField(label = "Directional Light ZFar")
    public double zFar = 10000;
    @MutableField(label = "Shadow map cutoff")
    public double cutoff = 50;
    @MutableField(label = "Shadow map attenuation min distance")
    public double shadowAttenuationMinDistance = 50;
    @MutableField(label = "Attenuation")
    public final Vector2f attenuation = new Vector2f();
    @MutableField(label = "Fallout smoothing")
    public double smoothing = 0.5;
    @MutableField(label = "Impact radius")
    public double radius = 45;
    @MutableField(label = "Directional light Size")
    public double size = 35;
    @MutableField(label = "Area light Radius")
    public double areaRadius = 1;
    @MutableField(label = "Area light Width")
    public double planeAreaWidth = 1;
    @MutableField(label = "Area light Height")
    public double planeAreaHeight = 1;
    @MutableField(label = "Intensity")
    public int intensity = 1;

    @EnumSelection(enumType = LightType.class)
    @MutableField(label = "Light type")
    public LightType type = LightType.DIRECTIONAL;

    @MutableField(label = "Area light radius")
    public final Color color = new Color();

    public final transient Vector2f atlasFace = new Vector2f();

    public LightComponent(Integer entityId) {
        super(entityId);
    }

    public LightComponent() {
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

