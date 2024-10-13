package com.pine.repository;

import com.pine.SerializableRepository;
import com.pine.injection.PBean;
import com.pine.inspection.Color;
import com.pine.inspection.Inspectable;
import com.pine.inspection.MutableField;
import com.pine.theme.Icons;

@PBean
public class EngineSettingsRepository extends Inspectable implements SerializableRepository {

    @MutableField(label = "Background color")
    public Color backgroundColor = new Color(0.24f, 0.24f, 0.24f);

    @MutableField(group = "Anti aliasing",label = "FXAA Enabled")
    public boolean fxaaEnabled = false;

    @MutableField(group = "Anti aliasing",label = "FXAA Span Max")
    public float fxaaSpanMax = 8f;

    @MutableField(group = "Anti aliasing",label = "FXAA Reduce Min")
    public float fxaaReduceMin = 1 / 128f;

    @MutableField(group = "Anti aliasing",label = "FXAA Reduce Multiplier")
    public float fxaaReduceMul = 1 / 8f;

    @MutableField(group = "Global illumination", label = "Enabled")
    public boolean ssgiEnabled = false;

    @MutableField(group = "Global illumination", label = "Blur Samples")
    public int ssgiBlurSamples = 5;

    @MutableField(group = "Global illumination", label = "Blur Radius")
    public float ssgiBlurRadius = 5f;

    @MutableField(group = "Global illumination", label = "Step Size")
    public float ssgiStepSize = 1f;

    @MutableField(group = "Global illumination", label = "Max Steps")
    public int ssgiMaxSteps = 4;

    @MutableField(group = "Global illumination", label = "Strength")
    public float ssgiStrength = 1f;

    @MutableField(group = "Screen space reflections", label = "Falloff")
    public float ssrFalloff = 3f;

    @MutableField(group = "Screen space reflections", label = "Step Size")
    public float ssrStepSize = 1f;

    @MutableField(group = "Screen space reflections", label = "Max Steps")
    public int ssrMaxSteps = 4;

    @MutableField(group = "Screen space shadows", label = "Max Distance")
    public float sssMaxDistance = .05f;

    @MutableField(group = "Screen space shadows", label = "Depth Thickness")
    public float sssDepthThickness = .05f;

    @MutableField(group = "Screen space shadows", label = "Edge Falloff")
    public float sssEdgeFalloff = 12f;

    @MutableField(group = "Screen space shadows", label = "Depth Delta")
    public float sssDepthDelta = 0f;

    @MutableField(group = "Screen space shadows", label = "Max Steps")
    public int sssMaxSteps = 24;

    @MutableField(group = "Ambient occlusion", label = "Enabled")
    public boolean ssaoEnabled = false;

    @MutableField(group = "Ambient occlusion", label = "Falloff Distance")
    public float ssaoFalloffDistance = 1000f;

    @MutableField(group = "Ambient occlusion", label = "Radius")
    public float ssaoRadius = .25f;

    @MutableField(group = "Ambient occlusion", label = "Power")
    public float ssaoPower = 1f;

    @MutableField(group = "Ambient occlusion", label = "Bias")
    public float ssaoBias = .1f;

    @MutableField(group = "Ambient occlusion", label = "Blur Samples")
    public int ssaoBlurSamples = 2;

    @MutableField(group = "Ambient occlusion", label = "Max Samples")
    public int ssaoMaxSamples = 64;

    @MutableField(label = "Physics Sub Steps")
    public int physicsSubSteps = 10;

    @MutableField(label = "Physics Simulation Step")
    public float physicsSimulationStep = 16.66666f;

    @MutableField(label = "Shadow Atlas Quantity")
    public int shadowAtlasQuantity = 4;

    @MutableField(label = "Shadow Map Resolution")
    public int shadowMapResolution = 4096;

    public DebugShadingModel debugShadingModel = DebugShadingModel.LIT;


    @Override
    public String getTitle() {
        return "Engine Settings";
    }

    @Override
    public String getIcon() {
        return Icons.display_settings;
    }
}