package com.pine.engine.core.service;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.pine.engine.Engine;
import com.pine.engine.core.service.camera.ICamera;
import com.pine.engine.core.service.camera.OCamera;
import com.pine.engine.core.service.camera.PCamera;

import java.util.HashMap;
import java.util.Map;

public class CameraService {
    private final Engine engine;
    private float pitch = 0.0f;
    private float yaw = -90.0f;
    private float sensitivity = 0.1f;
    private float lastMouseX, lastMouseY;
    private boolean firstMouseMove = true;
    private final String defaultPerspectiveCamera;
    private final String defaultOrthographicCamera;
    private final Map<String, ICamera> cameras = new HashMap<>();
    private float movementSpeed = 5.0f;
    private Camera currentCamera = null;

    public CameraService(Engine engine) {
        this.engine = engine;
        defaultOrthographicCamera = createNewCamera(true);
        defaultPerspectiveCamera = createNewCamera(false);
        currentCamera = (Camera) getCamera(defaultPerspectiveCamera);
    }

    public ICamera getCamera(String id) {
        return cameras.get(id);
    }

    public String createNewCamera(boolean isOrthographic) {
        ICamera newCamera;
        if (isOrthographic) {
            newCamera = new OCamera();
        } else {
            newCamera = new PCamera();
        }
        cameras.put(newCamera.getId(), newCamera);
        ((Camera) newCamera).position.set(0.0f, 0.0f, 5.0f);
        ((Camera) newCamera).lookAt(0.0f, 0.0f, 0.0f);
        ((Camera) newCamera).near = 0.1f;
        ((Camera) newCamera).far = 300.0f;
        ((Camera) newCamera).update();
        return newCamera.getId();
    }

    public void setCurrentCamera(String id) {
        if (cameras.containsKey(id)) {
            currentCamera = (Camera) cameras.get(id);
        }
    }

    public Map<String, ICamera> getCameras() {
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

    public void tick() {
        if (Gdx.input.isTouched() && engine.isInputFocused()) {
            handleMouseInput();
            handleKeyboardInput();
            currentCamera.update(true);
        } else {
            firstMouseMove = true;
        }
    }

    private void handleMouseInput() {
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.input.getY();

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
        float deltaTime = engine.getTotalTime();
        var forward = new Vector3(currentCamera.direction).nor();
        var right = new Vector3(currentCamera.direction).crs(currentCamera.up).nor(); // Right vector

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            currentCamera.position.add(forward.scl(movementSpeed * deltaTime)); // Move forward
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            currentCamera.position.sub(forward.scl(movementSpeed * deltaTime)); // Move backward
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            currentCamera.position.sub(right.scl(movementSpeed * deltaTime)); // Move left
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            currentCamera.position.add(right.scl(movementSpeed * deltaTime)); // Move right
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            currentCamera.position.add(currentCamera.up.scl(movementSpeed * deltaTime)); // Move up
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            currentCamera.position.sub(currentCamera.up.scl(movementSpeed * deltaTime)); // Move down
        }

        currentCamera.update();
    }

    private void updateCameraDirection() {
        currentCamera.direction.x = (float) Math.cos(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch));
        currentCamera.direction.y = (float) Math.sin(Math.toRadians(pitch));
        currentCamera.direction.z = (float) Math.sin(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch));
        currentCamera.direction.nor();
    }
}