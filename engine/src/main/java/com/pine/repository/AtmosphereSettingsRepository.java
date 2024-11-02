package com.pine.repository;

import com.pine.SerializableRepository;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.inspection.ExecutableField;
import com.pine.inspection.Inspectable;
import com.pine.inspection.InspectableField;
import com.pine.service.environment.EnvironmentMapGenService;
import com.pine.theme.Icons;
import com.pine.type.AtmosphereType;
import org.joml.Vector3f;

@PBean
public class AtmosphereSettingsRepository extends Inspectable implements SerializableRepository {

    @PInject
    public transient EnvironmentMapGenService environmentMapGenService;

    @ExecutableField(label = "Bake environment maps", icon = Icons.panorama_photosphere)
    public void process() {
        environmentMapGenService.bake();
    }

    @InspectableField(label = "Is enabled")
    public boolean enabled = false;
    @InspectableField(label = "Time of day")
    public float elapsedTime = 0;
    @InspectableField(label = "Max samples", max = 20, min = 1, isDirectChange = false, isAngle = false)
    public int maxSamples = 10;
    @InspectableField(label = "Mie height")
    public int mieHeight = 1000;
    @InspectableField(label = "Rayleigh Height")
    public int rayleighHeight = 8000;
    @InspectableField(label = "Atmosphere Radius", min = 1)
    public float atmosphereRadius = 1;
    @InspectableField(label = "Planet Radius", max = 1, min = 0)
    public float planetRadius = 1;
    @InspectableField(label = "Intensity", max = 20, min = 1, isDirectChange = false, isAngle = false)
    public float intensity = 10;
    @InspectableField(label = "Rendering Type")
    public AtmosphereType renderingType = AtmosphereType.COMBINED;
    @InspectableField(label = "Beta Rayleigh")
    public final Vector3f betaRayleigh = new Vector3f(1);
    @InspectableField(label = "Beta Mie")
    public final Vector3f betaMie = new Vector3f(1);
    @InspectableField(label = "Threshold", max = 10, min = -1, isDirectChange = false, isAngle = false)
    public float threshold = -.1f;

    @InspectableField(label = "Screen space shadows")
    public boolean screenSpaceShadows = false;

    @Override
    public String getTitle() {
        return "Atmosphere Settings";
    }

    @Override
    public String getIcon() {
        return Icons.cloud_sync;
    }
}