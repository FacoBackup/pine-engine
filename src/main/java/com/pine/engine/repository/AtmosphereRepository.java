package com.pine.engine.repository;

import com.pine.common.Icons;
import com.pine.common.SerializableRepository;
import com.pine.common.injection.PBean;
import com.pine.common.injection.PInject;
import com.pine.common.inspection.Color;
import com.pine.common.inspection.ExecutableField;
import com.pine.common.inspection.Inspectable;
import com.pine.common.inspection.InspectableField;
import com.pine.engine.service.environment.EnvironmentMapGenService;
import com.pine.engine.type.AtmosphereType;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

@PBean
public class AtmosphereRepository extends Inspectable implements SerializableRepository {

    @PInject
    public transient EnvironmentMapGenService environmentMapGenService;

    @ExecutableField(label = "Bake environment maps")
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
    public Color middayColor = new Color(.9f, .9f, .9f);

    @InspectableField(label = "Screen space shadows")
    public boolean screenSpaceShadows = false;

    @InspectableField(group = "Clouds", label = "Layer height", min = 1)
    public int cloudsHeight = 500;
    @InspectableField(group = "Clouds", label = "Layer width and depth", min = 1)
    public int cloudsSize = 100_000;
    @InspectableField(group = "Clouds", label = "Layer altitude")
    public int cloudsAltitude = 1000;

    @InspectableField(group = "Clouds", label = "Detail noise scale", min = 0)
    public float detailNoiseScale = 2;
    @InspectableField(group = "Clouds", label = "Detail scroll speed", min = 0)
    public float detailScrollSpeed = 1;
    @InspectableField(group = "Clouds", label = "Shape scroll speed", min = 0)
    public float shapeScrollSpeed = 1;
    @InspectableField(group = "Clouds", label = "Erosion strength", min = 1)
    public float cloudErosionStrength = 52;


    @InspectableField(group = "Clouds", label = "Density multiplier", min = 0)
    public float densityMultiplier = 1;
    @InspectableField(group = "Clouds", label = "Cloud coverage", min = 0)
    public float cloudCoverage = 0;
    @InspectableField(group = "Clouds", label = "Scale", min = 1)
    public float scale = 3;

    @InspectableField(group = "Clouds", label = "Light step count", max = 10, min = 0)
    public int numStepsLight = 5;
    @InspectableField(group = "Clouds", label = "Ray offset strength", min = 0)
    public float rayOffsetStrength = 10;

    @InspectableField(group = "Clouds", label = "Light absorption toward sun", min = 0, max = 1)
    public float lightAbsorptionTowardSun = .5f;
    @InspectableField(group = "Clouds", label = "Light absorption through cloud", min = 0, max = 1)
    public float lightAbsorptionThroughCloud = .175f;

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