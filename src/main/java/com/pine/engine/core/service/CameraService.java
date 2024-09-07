package com.pine.engine.core.service;

import com.pine.common.Updatable;
import com.pine.engine.Engine;
import com.pine.engine.core.EnvRepository;
import com.pine.engine.core.service.camera.AbstractCamera;
import com.pine.engine.core.service.camera.OrthographicCamera;
import com.pine.engine.core.service.camera.PerspectiveCamera;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public class CameraService implements Updatable {
    private final Engine engine;
    private final EnvRepository envRepository;
    private float pitch = 0.0f;
    private float yaw = -90.0f;
    private float sensitivity = 0.1f;
    private float lastMouseX, lastMouseY;
    private boolean firstMouseMove = true;
    private String defaultPerspectiveCamera;
    private String defaultOrthographicCamera;
    private final Map<String, AbstractCamera> cameras = new HashMap<>();
    private float movementSpeed = 5.0f;
    private AbstractCamera currentCamera = null;

    public CameraService(Engine engine) {
        this.engine = engine;
        this.envRepository = engine.getInputRepository();
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
        currentCamera.setViewportWidth(envRepository.getViewportW());
        currentCamera.setViewportHeight(envRepository.getViewportH());
        if (envRepository.isInputFocused()) {
            handleMouseInput();
            handleKeyboardInput();
            currentCamera.tick();
        } else {
            firstMouseMove = true;
        }
    }

    private void handleMouseInput() {
        float mouseX = envRepository.getMouseX();
        float mouseY = envRepository.getMouseY();

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
        if (envRepository.isForwardPressed()) {
            position.add(forward.mul(movementSpeed * deltaTime)); // Move forward
        }
        if (envRepository.isBackwardPressed()) {
            position.sub(forward.mul(movementSpeed * deltaTime)); // Move backward
        }
        if (envRepository.isLeftPressed()) {
            position.sub(right.mul(movementSpeed * deltaTime)); // Move left
        }
        if (envRepository.isRightPressed()) {
            position.add(right.mul(movementSpeed * deltaTime)); // Move right
        }
        if (envRepository.isUpPressed()) {
            position.add(currentCamera.getUp().mul(movementSpeed * deltaTime)); // Move up
        }
        if (envRepository.isDownPressed()) {
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
}