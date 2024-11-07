package com.pine.repository;

import com.pine.SerializableRepository;
import com.pine.injection.PBean;
import com.pine.inspection.Inspectable;
import com.pine.inspection.InspectableField;
import com.pine.theme.Icons;
import org.joml.Vector3f;

@PBean
public class CloudsRepository extends Inspectable implements SerializableRepository {
    @InspectableField(label = "Render clouds")
    public boolean enabled = false;

    @InspectableField(label = "Maximum number of steps")
    public int maxNumSteps = 128;

    @InspectableField(label = "Minimum cloud height in units (e.g., meters)")
    public float cloudMinHeight = 1500.0f;

    @InspectableField(label = "Maximum cloud height in units (e.g., meters)")
    public float cloudMaxHeight = 4000.0f;

    @InspectableField(label = "Scale for the shape noise used in clouds")
    public float shapeNoiseScale = 0.3f;

    @InspectableField(label = "Scale for detail noise used in clouds")
    public float detailNoiseScale = 5.5f;

    @InspectableField(label = "Modifier for detail noise intensity")
    public float detailNoiseModifier = 0.5f;

    @InspectableField(label = "Scale for turbulence noise in cloud formation")
    public float turbulenceNoiseScale = 7.44f;

    @InspectableField(label = "Amount of turbulence in clouds")
    public float turbulenceAmount = 1.0f;

    @InspectableField(label = "Cloud coverage percentage (0.0 - 1.0)")
    public float cloudCoverage = 0.7f;

    @InspectableField(label = "Angle of the wind in degrees or radians")
    public float windAngle = 0.0f;

    @InspectableField(label = "Speed of the wind in units (e.g., meters per second)")
    public float windSpeed = 50.0f;

    @InspectableField(label = "Offset for wind shear in clouds")
    public float windShearOffset = 500.0f;

    @InspectableField(label = "Direction of the wind as a vector")
    public Vector3f windDirection = new Vector3f(0.0f, 0.0f, 0.0f);

    @InspectableField(label = "Radius of the planet in units (e.g., meters)")
    public float planetRadius = 35000.0f;

    @InspectableField(label = "Center position of the planet")
    public Vector3f planetCenter = new Vector3f();

    @InspectableField(label = "Length of each light step")
    public float lightStepLength = 64.0f;

    @InspectableField(label = "Radius of the light cone for sunlight")
    public float lightConeRadius = 0.4f;

    @InspectableField(label = "Color of the sun as an RGB vector")
    public Vector3f sunColor = new Vector3f(1.0f, 1.0f, 1.0f);

    @InspectableField(label = "Base color of clouds")
    public Vector3f cloudBaseColor = new Vector3f(0.78f, 0.86f, 1.0f);

    @InspectableField(label = "Top color of clouds")
    public Vector3f cloudTopColor = new Vector3f(1.0f, 1.0f, 1.0f);

    @InspectableField(label = "Precipitation level")
    public float precipitation = 1.0f;

    @InspectableField(label = "Ambient light intensity")
    public float ambientLightFactor = 0.12f;

    @InspectableField(label = "Sunlight intensity")
    public float sunLightFactor = 1.0f;

    @InspectableField(label = "Forward scattering")
    public float henyeyGreensteinGForward = 0.4f;

    @InspectableField(label = "Backward scattering")
    public float henyeyGreensteinGBackward = 0.179f;

    @Override
    public String getTitle() {
        return "Cloud Settings";
    }

    @Override
    public String getIcon() {
        return Icons.cloud;
    }

}