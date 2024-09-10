package com.pine.engine.core.component;

import com.pine.engine.core.type.LightType;
import org.joml.Matrix4f;

import java.util.List;

public class LightComponent extends AbstractComponent {
    // TODO - BREAK INTO SEPARATED COMPONENT TYPES
    public boolean screenSpaceShadows = false;
    public double shadowBias = 0.0001;
    public int shadowSamples = 3;
    public double zNear = 1;
    public double zFar = 10000;
    public double cutoff = 50;
    public double shadowAttenuationMinDistance = 50;
    public double[] attenuation = {0, 0};
    public double smoothing = 0.5;
    public double radius = 45;
    public double size = 35;
    public double areaRadius = 1;
    public double planeAreaWidth = 1;
    public double planeAreaHeight = 1;
    public int intensity = 1;
    public LightType type = LightType.DIRECTIONAL;
    public int[] color = {255, 255, 255};
    public double[] fixedColor = {1, 1, 1};
    public boolean shadowMap = true;
    public int[] atlasFace = {0, 0};
    public final Matrix4f lightView = new Matrix4f();
    public final Matrix4f lightProjection = new Matrix4f();

    @Override
    public List<Class<? extends AbstractComponent>> getDependencies() {
        return List.of(TransformationComponent.class);
    }
}
