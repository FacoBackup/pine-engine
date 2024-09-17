package com.pine.engine.core.repository;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.pine.common.Initializable;
import com.pine.engine.core.EngineInjectable;
import com.pine.engine.core.service.camera.Camera;
import com.pine.engine.core.service.serialization.SerializableRepository;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

@EngineInjectable
public class CameraRepository extends SerializableRepository implements Initializable {
    private static final Gson GSON = new Gson();

    public Quaternionf pitchQ = new Quaternionf();
    public float pitch = 0.0f;
    public float yaw = -90;
    public float sensitivity = 0.1f;
    transient public float lastMouseX;
    transient public float lastMouseY;
    transient public boolean firstMouseMove = true;
    public float movementSpeed = 5.0f;
    transient public String defaultPerspectiveCamera;
    transient public String defaultOrthographicCamera;
    public final Map<String, Camera> cameras = new HashMap<>();
    transient public Camera currentCamera = null;
    transient public final Vector3f toApplyTranslation = new Vector3f();

    @Override
    public JsonElement serializeData() {
        return GSON.toJsonTree(this);
    }

    @Override
    protected void parseInternal(JsonElement data) {
//        JsonObject json = GSON.fromJson(data, JsonObject.class);
//        pitch = json.get("pitch").getAsFloat();
//        yaw = json.get("yaw").getAsFloat();
//        sensitivity = json.get("sensitivity").getAsFloat();
//        firstMouseMove = json.get("firstMouseMove").getAsBoolean();
//        movementSpeed = json.get("movementSpeed").getAsFloat();
//        json.get("cameras").getAsJsonArray().forEach(e -> {
//            JsonObject obj = e.getAsJsonObject();
//            Camera instance;
//            if (obj.get("isOrthographic").getAsBoolean()) {
//                instance = GSON.fromJson(e, OrthographicCamera.class);
//            } else {
//                instance = GSON.fromJson(e, PerspectiveCamera.class);
//            }
//
//            cameras.put(instance.getId(), instance);
//        });
    }

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
}
