package com.pine.engine.core.service.camera;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pine.common.EngineComponent;
import com.pine.engine.Engine;
import com.pine.engine.core.RuntimeRepository;
import com.pine.engine.core.service.serialization.SerializableRepository;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public class CameraService extends SerializableRepository implements EngineComponent {
    private static final Gson GSON = new Gson();
    transient private final Engine engine;
    transient private final RuntimeRepository runtimeRepository;
    private float pitch = 0.0f;
    private float yaw = -90.0f;
    private float sensitivity = 0.1f;
    transient private float lastMouseX;
    transient private float lastMouseY;
    private boolean firstMouseMove = true;
    private float movementSpeed = 5.0f;
    transient private String defaultPerspectiveCamera;
    transient private String defaultOrthographicCamera;
    private final Map<String, AbstractCamera> cameras = new HashMap<>();
    transient private AbstractCamera currentCamera = null;

    public CameraService(Engine engine) {
        this.engine = engine;
        this.runtimeRepository = engine.getRuntimeRepository();
    }

    @Override
    public void onInitialize() {
        defaultOrthographicCamera = createNewCamera(true);
        defaultPerspectiveCamera = createNewCamera(false);
        currentCamera = getCamera(defaultPerspectiveCamera);
    }

    public AbstractCamera getCamera(String id) {
        return cameras.get(id);
    }

    public String createNewCamera(boolean isOrthographic) {
        AbstractCamera newCamera;
        if (isOrthographic) {
            newCamera = new OrthographicCamera();
        } else {
            newCamera = new PerspectiveCamera();
        }
        cameras.put(newCamera.getId(), newCamera);
        newCamera.getPosition().set(0.0f, 0.0f, 5.0f);
        newCamera.lookAt(0.0f, 0.0f, 0.0f);
        newCamera.setNear(0.1f);
        newCamera.setFar(300.0f);
        newCamera.tick();
        return newCamera.getId();
    }

    public void setCurrentCamera(String id) {
        if (cameras.containsKey(id)) {
            currentCamera = cameras.get(id);
        }
    }

    public Map<String, AbstractCamera> getCameras() {
        return cameras;
    }

    public String getDefaultOrthographicCamera() {
        return defaultOrthographicCamera;
    }

    public String getDefaultPerspectiveCamera() {
        return defaultPerspectiveCamera;
    }

    public void removeCamera(String id) {
        if (defaultPerspectiveCamera.equals(id) || defaultOrthographicCamera.equals(id)) {
            return;
        }
        cameras.remove(id);
    }

    public void setSensitivity(float sensitivity) {
        this.sensitivity = sensitivity;
    }

    public float getSensitivity() {
        return sensitivity;
    }

    public void setMovementSpeed(float movementSpeed) {
        this.movementSpeed = movementSpeed;
    }

    public float getMovementSpeed() {
        return movementSpeed;
    }

    @Override
    public void tick() {
        currentCamera.setViewportWidth(runtimeRepository.getViewportW());
        currentCamera.setViewportHeight(runtimeRepository.getViewportH());
        if (runtimeRepository.isInputFocused()) {
            handleMouseInput();
            handleKeyboardInput();
            currentCamera.tick();
        } else {
            firstMouseMove = true;
        }
    }

    private void handleMouseInput() {
        float mouseX = runtimeRepository.getMouseX();
        float mouseY = runtimeRepository.getMouseY();

        if (firstMouseMove) {
            lastMouseX = mouseX;
            lastMouseY = mouseY;
            firstMouseMove = false;
        }

        float deltaX = (mouseX - lastMouseX) * sensitivity;
        float deltaY = (lastMouseY - mouseY) * sensitivity;

        yaw += deltaX;
        pitch -= deltaY;
        pitch = Math.max(-89.0f, Math.min(89.0f, pitch));

        updateCameraDirection();

        lastMouseX = mouseX;
        lastMouseY = mouseY;
    }

    private void handleKeyboardInput() {
        float deltaTime = engine.getClock().totalTime;
        Vector3f direction = currentCamera.getDirection();
        var forward = new Vector3f(direction).normalize();
        var right = direction.cross(currentCamera.getUp()).normalize(); // Right vector
        Vector3f position = currentCamera.getPosition();
        if (runtimeRepository.isForwardPressed()) {
            position.add(forward.mul(movementSpeed * deltaTime)); // Move forward
        }
        if (runtimeRepository.isBackwardPressed()) {
            position.sub(forward.mul(movementSpeed * deltaTime)); // Move backward
        }
        if (runtimeRepository.isLeftPressed()) {
            position.sub(right.mul(movementSpeed * deltaTime)); // Move left
        }
        if (runtimeRepository.isRightPressed()) {
            position.add(right.mul(movementSpeed * deltaTime)); // Move right
        }
        if (runtimeRepository.isUpPressed()) {
            position.add(currentCamera.getUp().mul(movementSpeed * deltaTime)); // Move up
        }
        if (runtimeRepository.isDownPressed()) {
            position.sub(currentCamera.getUp().mul(movementSpeed * deltaTime)); // Move down
        }

        currentCamera.tick();
    }

    private void updateCameraDirection() {
        Vector3f direction = currentCamera.getDirection();
        direction.x = (float) Math.cos(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch));
        direction.y = (float) Math.sin(Math.toRadians(pitch));
        direction.z = (float) Math.sin(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch));
        direction.normalize(direction);
    }

    @Override
    public JsonElement serializeData() {
        JsonElement jsonTree = GSON.toJsonTree(this);
        JsonObject obj = jsonTree.getAsJsonObject();
        obj.addProperty("currentCamera", currentCamera.getId());
        JsonArray cameras = new JsonArray();
        obj.add("cameras", cameras);

        for (var camera : this.cameras.values()) {
            JsonObject serialized = GSON.toJsonTree(camera).getAsJsonObject();
            serialized.addProperty("isOrthographic", camera instanceof OrthographicCamera);
            cameras.add(serialized);
        }
        return obj;
    }


    @Override
    protected void parseInternal(JsonElement data) {
        JsonObject json = GSON.fromJson(data, JsonObject.class);
        pitch = json.get("pitch").getAsFloat();
        yaw = json.get("yaw").getAsFloat();
        sensitivity = json.get("sensitivity").getAsFloat();
        firstMouseMove = json.get("firstMouseMove").getAsBoolean();
        movementSpeed = json.get("movementSpeed").getAsFloat();
        json.get("cameras").getAsJsonArray().forEach(e -> {
            JsonObject obj = e.getAsJsonObject();
            AbstractCamera instance;
            if (obj.get("isOrthographic").getAsBoolean()) {
                instance = GSON.fromJson(e, OrthographicCamera.class);
            } else {
                instance = GSON.fromJson(e, PerspectiveCamera.class);
            }

            cameras.put(instance.getId(), instance);
        });
    }
}