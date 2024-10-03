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
    @MutableField(label = "Camera rotation sensitivity")
    public float sensitivity = 1;

    @MutableField(label = "Camera Movement speed")
    public float movementSpeed = 5.0f;

    @MutableField(label = "Orbit camera zoom sensitivity")
    public float zoomSensitivity = 5.0f;

    @MutableField(label = "Motion Blur Enabled")
    public boolean motionBlurEnabled = false;

    @MutableField(label = "Motion Blur Velocity Scale")
    public float motionBlurVelocityScale = 1f;

    @MutableField(label = "Motion Blur Max Samples")
    public int motionBlurMaxSamples = 50;

    @MutableField(label = "Camera Motion Blur")
    public boolean cameraMotionBlur = false;

    @MutableField(label = "Bloom")
    public boolean bloom = false;

    @MutableField(label = "Film Grain")
    public boolean filmGrain = false;

    @MutableField(label = "Vignette Enabled")
    public boolean vignetteEnabled = false;

    @MutableField(label = "Chromatic Aberration")
    public boolean chromaticAberration = false;

    @MutableField(label = "Distortion")
    public boolean distortion = false;

    @MutableField(label = "Depth of Field (DOF)")
    public boolean DOF = false;

    @MutableField(label = "Focus Distance (DOF)")
    public int focusDistanceDOF = 10;

    @MutableField(label = "Aperture (DOF)")
    public float apertureDOF = 1.2f;

    @MutableField(label = "Focal Length (DOF)")
    public int focalLengthDOF = 5;

    @MutableField(label = "DOF Samples")
    public int samplesDOF = 100;

    @MutableField(label = "Film Grain Strength")
    public float filmGrainStrength = 1.f;

    @MutableField(label = "Vignette Strength")
    public float vignetteStrength = .25f;

    @MutableField(label = "Bloom Threshold")
    public float bloomThreshold = .75f;

    @MutableField(label = "Bloom Quality")
    public int bloomQuality = 8;

    @MutableField(label = "Bloom Offset")
    public int bloomOffset = 0;

    @MutableField(label = "Gamma")
    public float gamma = 2.2f;

    @MutableField(label = "Exposure")
    public float exposure = 1.f;

    @MutableField(label = "Chromatic Aberration Strength")
    public int chromaticAberrationStrength = 1;

    @MutableField(label = "Distortion Strength")
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
