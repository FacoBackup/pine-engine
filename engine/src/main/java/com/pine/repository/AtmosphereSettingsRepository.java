package com.pine.repository;

import com.pine.SerializableRepository;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.inspection.Color;
import com.pine.inspection.ExecutableField;
import com.pine.inspection.Inspectable;
import com.pine.inspection.InspectableField;
import com.pine.service.environment.EnvironmentMapGenService;
import com.pine.theme.Icons;
import com.pine.type.AtmosphereType;
import org.joml.Vector3f;
import org.joml.Vector4f;

@PBean
public class AtmosphereSettingsRepository extends Inspectable implements SerializableRepository {

    @PInject
    public transient EnvironmentMapGenService environmentMapGenService;
    @ExecutableField(label = "Bake environment maps", icon = Icons.panorama_photosphere)
    public void process() {
        environmentMapGenService.bake();
    }



    @InspectableField(label = "Atmospheric scattering")
    public boolean enabled = false;
    @InspectableField(label = "Volumetric Clouds")
    public boolean clouds;

    @InspectableField(label = "Time of day")
    public float elapsedTime = 0;

    @InspectableField(label = "Sun-light color")
    public Color sunLightColor = new Color(1, 1, 1);

    @InspectableField(label = "Screen space shadows")
    public boolean screenSpaceShadows = false;

    @InspectableField(group = "Atmospheric scattering", label = "Max samples", max = 20, min = 1)
    public int maxSamples = 10;
    @InspectableField(group = "Atmospheric scattering", label = "Mie height")
    public int mieHeight = 1000;
    @InspectableField(group = "Atmospheric scattering", label = "Rayleigh Height")
    public int rayleighHeight = 8000;
    @InspectableField(group = "Atmospheric scattering", label = "Atmosphere Radius", min = 1)
    public float atmosphereRadius = 1;
    @InspectableField(group = "Atmospheric scattering", label = "Planet Radius", max = 1, min = 0)
    public float planetRadius = 1;
    @InspectableField(group = "Atmospheric scattering", label = "Intensity", max = 20, min = 1, isDirectChange = false, isAngle = false)
    public float intensity = 10;
    @InspectableField(group = "Atmospheric scattering", label = "Rendering Type")
    public AtmosphereType renderingType = AtmosphereType.COMBINED;
    @InspectableField(group = "Atmospheric scattering", label = "Beta Rayleigh")
    public final Vector3f betaRayleigh = new Vector3f(1);
    @InspectableField(group = "Atmospheric scattering", label = "Beta Mie")
    public final Vector3f betaMie = new Vector3f(1);
    @InspectableField(group = "Atmospheric scattering", label = "Threshold", max = 10, min = -1, isDirectChange = false, isAngle = false)
    public float threshold = -.1f;

    @InspectableField(group = "Clouds", label = "densityMultiplier")
    public float densityMultiplier = 1;
    @InspectableField(group = "Clouds", label = "densityOffset")
    public float densityOffset = 0;
    @InspectableField(group = "Clouds", label = "scale")
    public float scale = .2f;
    @InspectableField(group = "Clouds", label = "detailNoiseScale")
    public float detailNoiseScale = 0;
    @InspectableField(group = "Clouds", label = "detailNoiseWeight")
    public float detailNoiseWeight = 0;
    @InspectableField(group = "Clouds", label = "detailWeights", min = 0)
    public Vector3f detailWeights = new Vector3f(0, 0, 1);
    @InspectableField(group = "Clouds", label = "shapeNoiseWeights")
    public Vector4f shapeNoiseWeights = new Vector4f(0, .1f, -1, 1);
    @InspectableField(group = "Clouds", label = "phaseParams")
    public Vector4f phaseParams = new Vector4f(0, 0, .2f, 0);
    @InspectableField(group = "Clouds", label = "numStepsLight", max = 10, min = 0)
    public int numStepsLight = 5;
    @InspectableField(group = "Clouds", label = "rayOffsetStrength")
    public float rayOffsetStrength = 1;
    @InspectableField(group = "Clouds", label = "boundsMin")
    public Vector3f boundsMin = new Vector3f(-1000, 200, -1000);
    @InspectableField(group = "Clouds", label = "boundsMax")
    public Vector3f boundsMax = new Vector3f(1000, 400, 1000);
    @InspectableField(group = "Clouds", label = "shapeOffset")
    public Vector3f shapeOffset = new Vector3f(0);
    @InspectableField(group = "Clouds", label = "detailOffset")
    public Vector3f detailOffset = new Vector3f(0);
    @InspectableField(group = "Clouds", label = "lightAbsorptionTowardSun")
    public float lightAbsorptionTowardSun = 1;
    @InspectableField(group = "Clouds", label = "lightAbsorptionThroughCloud")
    public float lightAbsorptionThroughCloud = .5f;
    @InspectableField(group = "Clouds", label = "darknessThreshold", min = 0, max = 1)
    public float darknessThreshold = 0;
    @InspectableField(group = "Clouds", label = "shapeScrollSpeed")
    public float shapeScrollSpeed = 1;
    @InspectableField(group = "Clouds", label = "detailScrollSpeed")
    public float detailScrollSpeed = 1;

    @Override
    public String getTitle() {
        return "Atmosphere Settings";
    }

    @Override
    public String getIcon() {
        return Icons.cloud_sync;
    }
}