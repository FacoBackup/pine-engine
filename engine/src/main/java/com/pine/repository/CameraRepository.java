package com.pine.repository;

import com.pine.SerializableRepository;
import com.pine.injection.PBean;
import com.pine.inspection.Inspectable;
import com.pine.inspection.InspectableField;
import com.pine.service.camera.Camera;
import com.pine.service.camera.Frustum;
import com.pine.theme.Icons;
import org.joml.Matrix4f;

@PBean
public class CameraRepository extends Inspectable implements SerializableRepository {
    @InspectableField(group = "Controls", label = "Camera rotation sensitivity")
    public float sensitivity = 1;

    @InspectableField(group = "Controls", label = "Camera Movement speed")
    public float movementSpeed = 1.0f;

    @InspectableField(group = "Controls", label = "Orbit camera zoom sensitivity")
    public float zoomSensitivity = 1.0f;

    @InspectableField(group = "Motion blur", label = "Motion Blur Enabled")
    public boolean motionBlurEnabled = false;

    @InspectableField(group = "Motion blur", label = "Motion Blur Velocity Scale")
    public float motionBlurVelocityScale = 1f;

    @InspectableField(group = "Motion blur", label = "Motion Blur Max Samples")
    public int motionBlurMaxSamples = 50;

    @InspectableField(group = "Motion blur", label = "Camera Motion Blur")
    public boolean cameraMotionBlur = false;

    @InspectableField(group = "Post processing", label = "Bloom")
    public boolean bloomEnabled = false;

    @InspectableField(group = "Post processing", label = "Film Grain")
    public boolean filmGrain = false;

    @InspectableField(group = "Post processing", label = "Vignette Enabled")
    public boolean vignetteEnabled = false;

    @InspectableField(group = "Post processing", label = "Chromatic Aberration")
    public boolean chromaticAberrationEnabled = false;

    @InspectableField(group = "Post processing", label = "Distortion")
    public boolean distortionEnabled = false;

    @InspectableField(group = "Depth of Field", label = "Enable DOF")
    public boolean DOF = false;

    @InspectableField(group = "Depth of Field", label = "Focus Distance (DOF)")
    public int focusDistanceDOF = 10;

    @InspectableField(group = "Depth of Field", label = "Aperture (DOF)")
    public float apertureDOF = 1.2f;

    @InspectableField(group = "Depth of Field", label = "Focal Length (DOF)")
    public int focalLengthDOF = 5;

    @InspectableField(group = "Depth of Field", label = "DOF Samples")
    public int samplesDOF = 100;

    @InspectableField(group = "Post processing", label = "Film Grain Strength")
    public float filmGrainStrength = 1.f;

    @InspectableField(group = "Post processing", label = "Vignette Strength")
    public float vignetteStrength = .25f;

    @InspectableField(group = "Post processing", label = "Bloom Threshold")
    public float bloomThreshold = .75f;

    @InspectableField(group = "Post processing", label = "Bloom Quality")
    public int bloomQuality = 8;

    @InspectableField(group = "Post processing", label = "Bloom Offset")
    public int bloomOffset = 0;

    @InspectableField(group = "Post processing", label = "Chromatic Aberration Strength", min = 0)
    public float chromaticAberrationIntensity = 1;

    @InspectableField(group = "Post processing", label = "Distortion Strength", min = 0)
    public float distortionIntensity = 1;

    public final Matrix4f viewMatrix = new Matrix4f();
    public final Matrix4f projectionMatrix = new Matrix4f();
    public final Matrix4f invViewMatrix = new Matrix4f();
    public final Matrix4f invProjectionMatrix = new Matrix4f();
    public final Matrix4f viewProjectionMatrix = new Matrix4f();
    public final Matrix4f skyboxProjectionMatrix = new Matrix4f();
    public final Matrix4f invSkyboxProjectionMatrix = new Matrix4f();
    public final Frustum frustum = new Frustum();
    public float lastMouseX;
    public float lastMouseY;
    public Camera currentCamera = new Camera();
    public float deltaX;
    public float deltaY;

    /**
     * Will force camera update if the instance is different from the current one
     *
     * @param camera
     */
    public void setCurrentCamera(Camera camera) {
        if (camera != null && camera != currentCamera) {
            currentCamera = camera;
            camera.registerChange();
        }
    }

    @Override
    public String getTitle() {
        return "Camera & lenses";
    }

    @Override
    public String getIcon() {
        return Icons.camera;
    }
}
