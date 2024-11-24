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
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

@PBean
public class AtmosphereRepository extends Inspectable implements SerializableRepository {

    @PInject
    public transient EnvironmentMapGenService environmentMapGenService;

    @ExecutableField(label = "Bake environment maps", icon = Icons.panorama_photosphere)
    public void process() {
        environmentMapGenService.bake();
    }

    @InspectableField(label = "Render atmosphere")
    public boolean enabled = false;

    @InspectableField(group = "Shadows", label = "Enabled")
    public boolean shadows = true;

    @InspectableField(group = "Shadows", label = "View size")
    public float shadowsViewSize = 10;

    @InspectableField(group = "Shadows", label = "Far plane")
    public float shadowsFarPlane = 100000;

    @InspectableField(group = "Shadows", label = "Near plane")
    public float shadowsNearPlane = .1f;

    @InspectableField(group = "Time", label = "Elapsed time")
    public float elapsedTime = .5f;

    @InspectableField(group = "Time", label = "Increment time")
    public boolean incrementTime = false;

    @InspectableField(group = "Time", label = "Time of day speed")
    public float elapsedTimeSpeed = 1;

    @InspectableField(group = "Sun", label = "Sun distance")
    public float sunDistance = 100_000;

    @InspectableField(group = "Sun", label = "Dawn color")
    public Color dawnColor = new Color(1, .39f, .19f);

    @InspectableField(group = "Sun", label = "Night color")
    public Color nightColor = new Color(.1f, .1f, .1f);

    @InspectableField(group = "Sun", label = "Midday color")
    public Color middayColor = new Color(1, 1, 1);

    @InspectableField(label = "Screen space shadows")
    public boolean screenSpaceShadows = false;

    @InspectableField(group = "Atmospheric scattering", label = "Max samples", max = 20, min = 1)
    public int maxSamples = 5;
    @InspectableField(group = "Atmospheric scattering", label = "Mie height")
    public int mieHeight = 1000;
    @InspectableField(group = "Atmospheric scattering", label = "Rayleigh Height")
    public int rayleighHeight = 8000;
    @InspectableField(group = "Atmospheric scattering", label = "Atmosphere Radius", min = 1)
    public float atmosphereRadius = 1;
    @InspectableField(group = "Atmospheric scattering", label = "Planet Radius", max = 1, min = 0)
    public float planetRadius = 1;
    @InspectableField(group = "Atmospheric scattering", label = "Intensity", max = 20, min = 1)
    public float intensity = 10;
    @InspectableField(group = "Atmospheric scattering", label = "Rendering Type")
    public AtmosphereType renderingType = AtmosphereType.COMBINED;
    @InspectableField(group = "Atmospheric scattering", label = "Beta Rayleigh")
    public final Vector3f betaRayleigh = new Vector3f(1);
    @InspectableField(group = "Atmospheric scattering", label = "Beta Mie")
    public final Vector3f betaMie = new Vector3f(1);
    @InspectableField(group = "Atmospheric scattering", label = "Threshold", max = 10, min = -1)
    public float threshold = -.1f;


    @InspectableField(group = "Clouds", label = "Layer height", min = 1)
    public int cloudsHeight = 500;
    @InspectableField(group = "Clouds", label = "Layer width and depth", min = 1)
    public int cloudsSize = 100_000;
    @InspectableField(group = "Clouds", label = "Layer altitude")
    public int cloudsAltitude = 1000;

    @InspectableField(group = "Cloud detail", label = "Detail noise scale", min = 0)
    public float detailNoiseScale = 1;
    @InspectableField(group = "Cloud detail", label = "Detail noise weight", min = 1)
    public float detailNoiseWeight = 1;
    @InspectableField(group = "Cloud detail", label = "Detail noise weights", min = 0)
    public Vector3f detailWeights = new Vector3f(0, 0, 1);
    @InspectableField(group = "Cloud detail", label = "Detail offset")
    public Vector3f detailOffset = new Vector3f(0);
    @InspectableField(group = "Cloud detail", label = "Detail scroll speed", min = 0, max = 1)
    public float detailScrollSpeed = 1;

    @InspectableField(group = "Cloud shape", label = "Shape noise weights")
    public Vector4f shapeNoiseWeights = new Vector4f(0, 0, 1, -.75f);
    @InspectableField(group = "Cloud shape", label = "Shape offset")
    public Vector3f shapeOffset = new Vector3f(0);
    @InspectableField(group = "Cloud shape", label = "Shape scroll speed", min = 0)
    public float shapeScrollSpeed = 1;

    @InspectableField(group = "Clouds", label = "Density multiplier", min = 0)
    public float densityMultiplier = 1;
    @InspectableField(group = "Clouds", label = "Density offset", min = 0)
    public float densityOffset = 0;
    @InspectableField(group = "Clouds", label = "Scale", min = 0)
    public float scale = .2f;

    @InspectableField(group = "Clouds", label = "Phase params")
    public Vector4f phaseParams = new Vector4f(0, 0, .3f, 1);
    @InspectableField(group = "Clouds", label = "Light step count", max = 10, min = 0)
    public int numStepsLight = 5;
    @InspectableField(group = "Clouds", label = "Ray offset strength", min = 0)
    public float rayOffsetStrength = 1;

    @InspectableField(group = "Clouds", label = "Light absorption toward sun", min = 0, max = 1)
    public float lightAbsorptionTowardSun = 1;
    @InspectableField(group = "Clouds", label = "Light absorption through cloud", min = 0, max = 1)
    public float lightAbsorptionThroughCloud = .5f;
    @InspectableField(group = "Clouds", label = "Darkness threshold", min = 0, max = 1)
    public float darknessThreshold = 0;

    public final Matrix4f lightSpaceMatrix = new Matrix4f();
    public final Matrix4f lightViewMatrix = new Matrix4f();
    public final Matrix4f lightProjectionMatrix = new Matrix4f();

    @Override
    public String getTitle() {
        return "Atmosphere Settings";
    }

    @Override
    public String getIcon() {
        return Icons.cloud_sync;
    }
}