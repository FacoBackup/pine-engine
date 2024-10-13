package com.pine.repository;

import com.pine.SerializableRepository;
import com.pine.injection.PBean;
import com.pine.inspection.Inspectable;
import com.pine.inspection.MutableField;
import com.pine.service.camera.Camera;
import com.pine.service.camera.Frustum;
import com.pine.theme.Icons;
import org.joml.Matrix4f;

@PBean
public class CameraRepository extends Inspectable implements SerializableRepository {
    @MutableField(group = "Controls", label = "Camera rotation sensitivity")
    public float sensitivity = 1;

    @MutableField(group = "Controls", label = "Camera Movement speed")
    public float movementSpeed = 1.0f;

    @MutableField(group = "Controls", label = "Orbit camera zoom sensitivity")
    public float zoomSensitivity = 1.0f;

    @MutableField(group = "Motion blur", label = "Motion Blur Enabled")
    public boolean motionBlurEnabled = false;

    @MutableField(group = "Motion blur", label = "Motion Blur Velocity Scale")
    public float motionBlurVelocityScale = 1f;

    @MutableField(group = "Motion blur", label = "Motion Blur Max Samples")
    public int motionBlurMaxSamples = 50;

    @MutableField(group = "Motion blur", label = "Camera Motion Blur")
    public boolean cameraMotionBlur = false;

    @MutableField(group = "Post processing", label = "Bloom")
    public boolean bloom = false;

    @MutableField(group = "Post processing", label = "Film Grain")
    public boolean filmGrain = false;

    @MutableField(group = "Post processing", label = "Vignette Enabled")
    public boolean vignetteEnabled = false;

    @MutableField(group = "Post processing", label = "Chromatic Aberration")
    public boolean chromaticAberration = false;

    @MutableField(group = "Post processing", label = "Distortion")
    public boolean distortion = false;

    @MutableField(group = "Depth of Field", label = "Enable DOF")
    public boolean DOF = false;

    @MutableField(group = "Depth of Field", label = "Focus Distance (DOF)")
    public int focusDistanceDOF = 10;

    @MutableField(group = "Depth of Field", label = "Aperture (DOF)")
    public float apertureDOF = 1.2f;

    @MutableField(group = "Depth of Field", label = "Focal Length (DOF)")
    public int focalLengthDOF = 5;

    @MutableField(group = "Depth of Field", label = "DOF Samples")
    public int samplesDOF = 100;

    @MutableField(group = "Post processing", label = "Film Grain Strength")
    public float filmGrainStrength = 1.f;

    @MutableField(group = "Post processing", label = "Vignette Strength")
    public float vignetteStrength = .25f;

    @MutableField(group = "Post processing", label = "Bloom Threshold")
    public float bloomThreshold = .75f;

    @MutableField(group = "Post processing", label = "Bloom Quality")
    public int bloomQuality = 8;

    @MutableField(group = "Post processing", label = "Bloom Offset")
    public int bloomOffset = 0;

    @MutableField(group = "Post processing", label = "Gamma")
    public float gamma = 2.2f;

    @MutableField(group = "Post processing", label = "Exposure")
    public float exposure = 1.f;

    @MutableField(group = "Post processing", label = "Chromatic Aberration Strength")
    public int chromaticAberrationStrength = 1;

    @MutableField(group = "Post processing", label = "Distortion Strength")
    public int distortionStrength = 1;

    public final Matrix4f viewMatrix = new Matrix4f();
    public final Matrix4f projectionMatrix = new Matrix4f();
    public final Matrix4f invViewMatrix = new Matrix4f();
    public final Matrix4f invProjectionMatrix = new Matrix4f();
    public final Matrix4f viewProjectionMatrix = new Matrix4f();
    public final Matrix4f staticViewMatrix = new Matrix4f();
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

    public Camera getCurrentCamera() {
        return currentCamera;
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
