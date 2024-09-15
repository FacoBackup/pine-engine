package com.pine.engine.core.component;

import com.pine.engine.core.EngineInjectable;

import java.util.List;
import java.util.Set;

@EngineInjectable
public class CameraComponent extends AbstractComponent<CameraComponent> {
    public float fov = 45.0f;
    public boolean dynamicAspectRatio = true;
    public float aspectRatio = 1.0f;
    public float zFar = 100.0f;
    public float zNear = 0.1f;
    public boolean distortion = false;
    public float distortionStrength = 1.0f;
    public boolean chromaticAberration = false;
    public float chromaticAberrationStrength = 1.0f;
    public boolean vignette = false;
    public float vignetteStrength = 0.25f;
    public boolean filmGrain = false;
    public float filmGrainStrength = 1.0f;
    public boolean bloom = false;
    public float mbVelocityScale = 1.0f;
    public int mbSamples = 50;
    public float bloomThreshold = 0.75f;
    public int bloomQuality = 8;
    public float bloomOffset = 0.0f;
    public float gamma = 2.2f;
    public float exposure = 1.0f;
    public boolean motionBlurEnabled = true;
    public boolean cameraMotionBlur = false;
    public boolean ortho = false;
    public float size = 10.0f;
    public float apertureDOF = 1.2f;
    public float focalLengthDOF = 10.0f;
    public float focusDistanceDOF = 100.0f;
    public int samplesDOF = 100;
    public boolean enabledDOF = false;

    public CameraComponent(Integer entityId) {
        super(entityId);
    }

    public CameraComponent() {
        super();
    }

    @Override
    protected Set<Class<? extends AbstractComponent>> getDependenciesInternal() {
        return Set.of(TransformationComponent.class);
    }
}
