package com.pine.repository;

import com.pine.SerializableRepository;
import com.pine.injection.PBean;
import com.pine.inspection.Inspectable;
import com.pine.inspection.InspectableField;
import com.pine.theme.Icons;
import com.pine.type.AtmosphereType;
import org.joml.Vector3f;

@PBean
public class AtmosphereSettingsRepository extends Inspectable implements SerializableRepository {
    @InspectableField(label = "Is enabled")
    public boolean enabled = false;
    @InspectableField(label = "Time of day")
    public float elapsedTime = 0;
    @InspectableField(label = "Max samples", max = 20, min = 1, isDirectChange = false, isAngle = false)
    public int maxSamples = 10;
    @InspectableField(label = "Mie height")
    public float mieHeight = 1000;
    @InspectableField(label = "Rayleigh Height")
    public float rayleighHeight = 8000;
    @InspectableField(label = "Atmosphere Radius")
    public float atmosphereRadius = 1;
    @InspectableField(label = "Planet Radius")
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

    @Override
    public String getTitle() {
        return "Atmosphere Settings";
    }

    @Override
    public String getIcon() {
        return Icons.cloud_sync;
    }
}