package com.pine.repository;

import com.pine.SerializableRepository;
import com.pine.injection.PBean;
import com.pine.inspection.Inspectable;
import com.pine.inspection.InspectableField;
import com.pine.theme.Icons;

@PBean
public class EngineSettingsRepository extends Inspectable implements SerializableRepository {

    public boolean disableCullingGlobally = false;

    @InspectableField(label = "Probe capture resolution")
    public int probeCaptureResolution = 128;

    @InspectableField(label = "Probe filtering levels", min = 1, max = 10)
    public int probeFiltering = 5;

    @InspectableField(group = "Anti aliasing",label = "FXAA Enabled")
    public boolean fxaaEnabled = true;

    @InspectableField(group = "Anti aliasing",label = "FXAA Span Max")
    public float fxaaSpanMax = 8f;

    @InspectableField(group = "Anti aliasing",label = "FXAA Reduce Min")
    public float fxaaReduceMin = 1 / 128f;

    @InspectableField(group = "Anti aliasing",label = "FXAA Reduce Multiplier")
    public float fxaaReduceMul = 1 / 8f;

    @InspectableField(group = "Global illumination", label = "Enabled")
    public boolean ssgiEnabled = false;

    @InspectableField(group = "Global illumination", label = "Blur Samples")
    public int ssgiBlurSamples = 5;

    @InspectableField(group = "Global illumination", label = "Blur Radius")
    public float ssgiBlurRadius = 5f;

    @InspectableField(group = "Global illumination", label = "Step Size")
    public float ssgiStepSize = 1f;

    @InspectableField(group = "Global illumination", label = "Max Steps")
    public int ssgiMaxSteps = 4;

    @InspectableField(group = "Global illumination", label = "Strength")
    public float ssgiStrength = 1f;

    @InspectableField(group = "Screen space reflections", label = "Falloff")
    public float ssrFalloff = 3f;

    @InspectableField(group = "Screen space reflections", label = "Step Size")
    public float ssrStepSize = 1f;

    @InspectableField(group = "Screen space reflections", label = "Max Steps")
    public int ssrMaxSteps = 4;

    @InspectableField(group = "Screen space shadows", label = "Max Distance")
    public float sssMaxDistance = .05f;

    @InspectableField(group = "Screen space shadows", label = "Depth Thickness")
    public float sssDepthThickness = .05f;

    @InspectableField(group = "Screen space shadows", label = "Edge Falloff")
    public float sssEdgeFalloff = 12f;

    @InspectableField(group = "Screen space shadows", label = "Depth Delta")
    public float sssDepthDelta = 0f;

    @InspectableField(group = "Screen space shadows", label = "Max Steps")
    public int sssMaxSteps = 24;

    @InspectableField(group = "Ambient occlusion", label = "Enabled")
    public boolean ssaoEnabled = false;

    @InspectableField(group = "Ambient occlusion", label = "Falloff Distance")
    public float ssaoFalloffDistance = 1000f;

    @InspectableField(group = "Ambient occlusion", label = "Radius")
    public float ssaoRadius = .25f;

    @InspectableField(group = "Ambient occlusion", label = "Power")
    public float ssaoPower = 1f;

    @InspectableField(group = "Ambient occlusion", label = "Bias")
    public float ssaoBias = .1f;

    @InspectableField(group = "Ambient occlusion", label = "Blur Samples")
    public int ssaoBlurSamples = 2;

    @InspectableField(group = "Ambient occlusion", label = "Max Samples")
    public int ssaoMaxSamples = 64;


    public DebugShadingModel debugShadingModel = DebugShadingModel.LIT;
    public boolean gridOverlay = false;

    @Override
    public String getTitle() {
        return "Engine Settings";
    }

    @Override
    public String getIcon() {
        return Icons.display_settings;
    }
}