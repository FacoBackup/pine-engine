package com.pine.engine.core.service.camera;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pine.common.Updatable;
import com.pine.common.Initializable;
import com.pine.engine.core.repository.CameraRepository;
import com.pine.engine.core.repository.ClockRepository;
import com.pine.engine.core.repository.RuntimeRepository;
import com.pine.engine.core.service.AbstractMultithreadedService;
import com.pine.engine.core.service.serialization.SerializableRepository;
import com.pine.engine.core.EngineDependency;
import com.pine.engine.core.EngineInjectable;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

@EngineInjectable
public class CameraService extends AbstractMultithreadedService {

    @EngineDependency
    public CameraRepository repository;

    @EngineDependency
    public RuntimeRepository runtimeRepository;

    @EngineDependency
    public ClockRepository clock;

    @Override
    public void lateInitialize() {
        repository.defaultOrthographicCamera = createNewCamera(true);
        repository.defaultPerspectiveCamera = createNewCamera(false);
        repository.currentCamera = getCamera(repository.defaultPerspectiveCamera);
        super.lateInitialize();
    }

    public AbstractCamera getCamera(String id) {
        return repository.cameras.get(id);
    }

    public String createNewCamera(boolean isOrthographic) {
        AbstractCamera newCamera;
        if (isOrthographic) {
            newCamera = new OrthographicCamera();
        } else {
            newCamera = new PerspectiveCamera();
        }
        repository.cameras.put(newCamera.getId(), newCamera);
        newCamera.getPosition().set(0.0f, 0.0f, 5.0f);
        newCamera.lookAt(0.0f, 0.0f, 0.0f);
        newCamera.setNear(0.1f);
        newCamera.setFar(300.0f);
        newCamera.tick();
        return newCamera.getId();
    }

    public void setCurrentCamera(String id) {
        if (repository.cameras.containsKey(id)) {
            repository.currentCamera = repository.cameras.get(id);
        }
    }

    public Map<String, AbstractCamera> getCameras() {
        return repository.cameras;
    }

    public String getDefaultOrthographicCamera() {
        return repository.defaultOrthographicCamera;
    }

    public String getDefaultPerspectiveCamera() {
        return repository.defaultPerspectiveCamera;
    }

    public void removeCamera(String id) {
        if (repository.defaultPerspectiveCamera.equals(id) || repository.defaultOrthographicCamera.equals(id)) {
            return;
        }
        repository.cameras.remove(id);
    }

    public void setSensitivity(float sensitivity) {
        repository.sensitivity = sensitivity;
    }

    public float getSensitivity() {
        return repository.sensitivity;
    }

    public void setMovementSpeed(float movementSpeed) {
        repository.movementSpeed = movementSpeed;
    }

    public float getMovementSpeed() {
        return repository.movementSpeed;
    }


    @Override
    protected void tickInternal() {
        repository.currentCamera.setViewportWidth(runtimeRepository.getViewportW());
        repository.currentCamera.setViewportHeight(runtimeRepository.getViewportH());
        if (runtimeRepository.isInputFocused()) {
            handleMouseInput();
            handleKeyboardInput();
            repository.currentCamera.tick();
        } else {
            repository.firstMouseMove = true;
        }
//        CameraManager.updateUBOs()
    }

    private void handleMouseInput() {
        float mouseX = runtimeRepository.getMouseX();
        float mouseY = runtimeRepository.getMouseY();

        if (repository.firstMouseMove) {
            repository.lastMouseX = mouseX;
            repository.lastMouseY = mouseY;
            repository.firstMouseMove = false;
        }

        float deltaX = (mouseX - repository.lastMouseX) * repository.sensitivity;
        float deltaY = (repository.lastMouseY - mouseY) * repository.sensitivity;

        repository.yaw += deltaX;
        repository.pitch -= deltaY;
        repository.pitch = Math.max(-89.0f, Math.min(89.0f, repository.pitch));

        updateCameraDirection();

        repository.lastMouseX = mouseX;
        repository.lastMouseY = mouseY;
    }

    private void handleKeyboardInput() {
        float deltaTime = clock.totalTime;
        Vector3f direction = repository.currentCamera.getDirection();
        var forward = new Vector3f(direction).normalize();
        var right = direction.cross(repository.currentCamera.getUp()).normalize(); // Right vector
        Vector3f position = repository.currentCamera.getPosition();
        if (runtimeRepository.isForwardPressed()) {
            position.add(forward.mul(repository.movementSpeed * deltaTime)); // Move forward
        }
        if (runtimeRepository.isBackwardPressed()) {
            position.sub(forward.mul(repository.movementSpeed * deltaTime)); // Move backward
        }
        if (runtimeRepository.isLeftPressed()) {
            position.sub(right.mul(repository.movementSpeed * deltaTime)); // Move left
        }
        if (runtimeRepository.isRightPressed()) {
            position.add(right.mul(repository.movementSpeed * deltaTime)); // Move right
        }
        if (runtimeRepository.isUpPressed()) {
            position.add(repository.currentCamera.getUp().mul(repository.movementSpeed * deltaTime)); // Move up
        }
        if (runtimeRepository.isDownPressed()) {
            position.sub(repository.currentCamera.getUp().mul(repository.movementSpeed * deltaTime)); // Move down
        }

        repository.currentCamera.tick();
    }

    private void updateCameraDirection() {
        Vector3f direction = repository.currentCamera.getDirection();
        direction.x = (float) Math.cos(Math.toRadians(repository.yaw)) * (float) Math.cos(Math.toRadians(repository.pitch));
        direction.y = (float) Math.sin(Math.toRadians(repository.pitch));
        direction.z = (float) Math.sin(Math.toRadians(repository.yaw)) * (float) Math.cos(Math.toRadians(repository.pitch));
        direction.normalize(direction);
    }

}