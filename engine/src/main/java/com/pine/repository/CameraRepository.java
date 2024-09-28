package com.pine.repository;

import com.pine.Initializable;
import com.pine.PBean;
import com.pine.SerializableRepository;
import com.pine.inspection.Inspectable;
import com.pine.inspection.MutableField;
import com.pine.service.camera.Camera;
import com.pine.service.camera.Frustum;
import com.pine.theme.Icons;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

@PBean
public class CameraRepository extends Inspectable implements Initializable, SerializableRepository {
    public Map<String, Camera> cameras = new HashMap<>();

    @MutableField(label = "Sensitivity")
    public float sensitivity = 0.1f;
    @MutableField(label = "Movement speed")
    public float movementSpeed = 5.0f;

    transient public Quaternionf pitchQ = new Quaternionf();
    public float pitch = 0.0f;
    public float yaw = -90;
    public float lastMouseX;
    public float lastMouseY;
    public boolean firstMouseMove = true;
    public String defaultPerspectiveCamera;
    public String defaultOrthographicCamera;
    public Camera currentCamera = null;
    transient public final Frustum frustum = new Frustum();
    transient public final Vector3f toApplyTranslation = new Vector3f();

    @Override
    public void onInitialize() {
        var defaultOrthographicCameraI = new Camera();
        var defaultPerspectiveCameraI = new Camera();
        defaultOrthographicCameraI.isOrthographic = true;

        cameras.put(defaultOrthographicCameraI.id, defaultOrthographicCameraI);
        cameras.put(defaultPerspectiveCameraI.id, defaultPerspectiveCameraI);

        defaultOrthographicCamera = defaultOrthographicCameraI.id;
        defaultPerspectiveCamera = defaultPerspectiveCameraI.id;

        currentCamera = defaultPerspectiveCameraI;
    }

    public Camera getCamera(String id) {
        return cameras.get(id);
    }

    public void setCurrentCamera(String id) {
        if (cameras.containsKey(id)) {
            currentCamera = cameras.get(id);
        }
    }

    public void removeCamera(String id) {
        if (defaultPerspectiveCamera.equals(id) || defaultOrthographicCamera.equals(id)) {
            return;
        }
        cameras.remove(id);
    }

    @Override
    public String getTitle() {
        return "Camera & lens";
    }

    @Override
    public String getIcon() {
        return Icons.camera;
    }
}
