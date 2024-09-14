package com.pine.engine.core.service.camera;

import com.pine.engine.core.EngineDependency;
import com.pine.engine.core.EngineInjectable;
import com.pine.engine.core.EngineUtils;
import com.pine.engine.core.repository.CameraRepository;
import com.pine.engine.core.repository.ClockRepository;
import com.pine.engine.core.repository.CoreResourceRepository;
import com.pine.engine.core.repository.RuntimeRepository;
import com.pine.engine.core.service.AbstractMultithreadedService;
import com.pine.engine.core.service.resource.UBOService;
import org.joml.Vector3f;

import java.util.Map;

@EngineInjectable
public class CameraService extends AbstractMultithreadedService {
    private static final double LOG_2 = Math.log(2);

    @EngineDependency
    public CameraRepository repository;

    @EngineDependency
    public CoreResourceRepository coreResourceRepository;

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
        newCamera.position.set(0.0f, 0.0f, 5.0f);
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
        repository.currentCamera.setViewportWidth(runtimeRepository.viewportW);
        repository.currentCamera.setViewportHeight(runtimeRepository.viewportH);
        if (runtimeRepository.inputFocused) {
            handleMouseInput();
            handleKeyboardInput();
            repository.currentCamera.tick();
        } else {
            repository.firstMouseMove = true;
        }

        var V = coreResourceRepository.cameraViewUBOState;
        EngineUtils.copyWithOffset(V, repository.currentCamera.viewProjectionMatrix, 0);
        EngineUtils.copyWithOffset(V, repository.currentCamera.viewMatrix, 16);
        EngineUtils.copyWithOffset(V, repository.currentCamera.invViewMatrix, 32);
        EngineUtils.copyWithOffset(V, repository.currentCamera.position, 48);

        var P = coreResourceRepository.cameraProjectionUBOState;
        EngineUtils.copyWithOffset(P, repository.currentCamera.projectionMatrix, 0);
        EngineUtils.copyWithOffset(P, repository.currentCamera.invProjectionMatrix, 16);

        P.put(32, runtimeRepository.viewportW);
        P.put(33, runtimeRepository.viewportH);
        P.put(34, (float) (2.0 / (Math.log(repository.currentCamera.projectionMatrix.get(0, 0) + 1) / LOG_2)));
    }

    private void handleMouseInput() {
        float mouseX = runtimeRepository.mouseX;
        float mouseY = runtimeRepository.mouseY;

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
        final float deltaTime = clock.totalTime;
        final Vector3f direction = repository.currentCamera.direction;
        final var forward = new Vector3f(direction).normalize();
        final var right = direction.cross(repository.currentCamera.up).normalize(); // Right vector
        final Vector3f position = repository.currentCamera.position;
        if (runtimeRepository.forwardPressed) {
            position.add(forward.mul(repository.movementSpeed * deltaTime)); // Move forward
        }
        if (runtimeRepository.backwardPressed) {
            position.sub(forward.mul(repository.movementSpeed * deltaTime)); // Move backward
        }
        if (runtimeRepository.leftPressed) {
            position.sub(right.mul(repository.movementSpeed * deltaTime)); // Move left
        }
        if (runtimeRepository.rightPressed) {
            position.add(right.mul(repository.movementSpeed * deltaTime)); // Move right
        }
        if (runtimeRepository.upPressed) {
            position.add(repository.currentCamera.up.mul(repository.movementSpeed * deltaTime)); // Move up
        }
        if (runtimeRepository.downPressed) {
            position.sub(repository.currentCamera.up.mul(repository.movementSpeed * deltaTime)); // Move down
        }

        repository.currentCamera.tick();
    }

    private void updateCameraDirection() {
        Vector3f direction = repository.currentCamera.direction;
        direction.x = (float) Math.cos(Math.toRadians(repository.yaw)) * (float) Math.cos(Math.toRadians(repository.pitch));
        direction.y = (float) Math.sin(Math.toRadians(repository.pitch));
        direction.z = (float) Math.sin(Math.toRadians(repository.yaw)) * (float) Math.cos(Math.toRadians(repository.pitch));
        direction.normalize(direction);
    }

}