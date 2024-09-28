package com.pine.repository;

import com.pine.PBean;
import com.pine.SerializableRepository;
import com.pine.inspection.Inspectable;
import com.pine.inspection.MutableField;
import com.pine.theme.Icons;

@PBean
public class EngineSettingsRepository extends Inspectable implements SerializableRepository {

    @MutableField(label = "FXAA Enabled")
    public boolean fxaaEnabled = false;

    @MutableField(label = "FXAA Span Max")
    public float fxaaSpanMax = 8f;

    @MutableField(label = "FXAA Reduce Min")
    public float fxaaReduceMin = 1 / 128f;

    @MutableField(label = "FXAA Reduce Multiplier")
    public float fxaaReduceMul = 1 / 8f;

    @MutableField(label = "SSGI Enabled")
    public boolean ssgiEnabled = false;

    @MutableField(label = "SSGI Blur Samples")
    public int ssgiBlurSamples = 5;

    @MutableField(label = "SSGI Blur Radius")
    public float ssgiBlurRadius = 5f;

    @MutableField(label = "SSGI Step Size")
    public float ssgiStepSize = 1f;

    @MutableField(label = "SSGI Max Steps")
    public int ssgiMaxSteps = 4;

    @MutableField(label = "SSGI Strength")
    public float ssgiStrength = 1f;

    @MutableField(label = "SSR Falloff")
    public float ssrFalloff = 3f;

    @MutableField(label = "SSR Step Size")
    public float ssrStepSize = 1f;

    @MutableField(label = "SSR Max Steps")
    public int ssrMaxSteps = 4;

    @MutableField(label = "SSS Max Distance")
    public float sssMaxDistance = .05f;

    @MutableField(label = "SSS Depth Thickness")
    public float sssDepthThickness = .05f;

    @MutableField(label = "SSS Edge Falloff")
    public float sssEdgeFalloff = 12f;

    @MutableField(label = "SSS Depth Delta")
    public float sssDepthDelta = 0f;

    @MutableField(label = "SSS Max Steps")
    public int sssMaxSteps = 24;

    @MutableField(label = "SSAO Enabled")
    public boolean ssaoEnabled = false;

    @MutableField(label = "SSAO Falloff Distance")
    public float ssaoFalloffDistance = 1000f;

    @MutableField(label = "SSAO Radius")
    public float ssaoRadius = .25f;

    @MutableField(label = "SSAO Power")
    public float ssaoPower = 1f;

    @MutableField(label = "SSAO Bias")
    public float ssaoBias = .1f;

    @MutableField(label = "SSAO Blur Samples")
    public int ssaoBlurSamples = 2;

    @MutableField(label = "SSAO Max Samples")
    public int ssaoMaxSamples = 64;

    @MutableField(label = "Physics Sub Steps")
    public int physicsSubSteps = 10;

    @MutableField(label = "Physics Simulation Step")
    public float physicsSimulationStep = 16.66666f;

    @MutableField(label = "Shadow Atlas Quantity")
    public int shadowAtlasQuantity = 4;

    @MutableField(label = "Shadow Map Resolution")
    public int shadowMapResolution = 4096;

    @MutableField(label = "Motion Blur Enabled")
    public boolean motionBlurEnabled = false;

    @MutableField(label = "Motion Blur Velocity Scale")
    public float motionBlurVelocityScale = 1f;

    @MutableField(label = "Motion Blur Max Samples")
    public int motionBlurMaxSamples = 50;

    @MutableField(label = "Camera Motion Blur")
    public boolean cameraMotionBlur = false;

    @MutableField(label = "Bloom")
    public boolean bloom = false;

    @MutableField(label = "Film Grain")
    public boolean filmGrain = false;

    @MutableField(label = "Vignette Enabled")
    public boolean vignetteEnabled = false;

    @MutableField(label = "Chromatic Aberration")
    public boolean chromaticAberration = false;

    @MutableField(label = "Distortion")
    public boolean distortion = false;

    @MutableField(label = "Depth of Field (DOF)")
    public boolean DOF = false;

    @MutableField(label = "Focus Distance (DOF)")
    public int focusDistanceDOF = 10;

    @MutableField(label = "Aperture (DOF)")
    public float apertureDOF = 1.2f;

    @MutableField(label = "Focal Length (DOF)")
    public int focalLengthDOF = 5;

    @MutableField(label = "DOF Samples")
    public int samplesDOF = 100;

    @MutableField(label = "Film Grain Strength")
    public float filmGrainStrength = 1.f;

    @MutableField(label = "Vignette Strength")
    public float vignetteStrength = .25f;

    @MutableField(label = "Bloom Threshold")
    public float bloomThreshold = .75f;

    @MutableField(label = "Bloom Quality")
    public int bloomQuality = 8;

    @MutableField(label = "Bloom Offset")
    public int bloomOffset = 0;

    @MutableField(label = "Gamma")
    public float gamma = 2.2f;

    @MutableField(label = "Exposure")
    public float exposure = 1.f;

    @MutableField(label = "Chromatic Aberration Strength")
    public int chromaticAberrationStrength = 1;

    @MutableField(label = "Distortion Strength")
    public int distortionStrength = 1;

    @Override
    public String getTitle() {
        return "Engine Settings";
    }

    @Override
    public String getIcon() {
        return Icons.display_settings;
    }
}