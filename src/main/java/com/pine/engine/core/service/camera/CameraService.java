package com.pine.engine.core.service.camera;

import com.pine.common.Loggable;
import com.pine.engine.core.EngineDependency;
import com.pine.engine.core.EngineInjectable;
import com.pine.engine.core.EngineUtils;
import com.pine.engine.core.repository.CameraRepository;
import com.pine.engine.core.repository.ClockRepository;
import com.pine.engine.core.repository.CoreResourceRepository;
import com.pine.engine.core.repository.RuntimeRepository;
import com.pine.engine.core.service.AbstractMultithreadedService;
import org.joml.Quaternionf;

@EngineInjectable
public class CameraService extends AbstractMultithreadedService implements Loggable {
    private static final double LOG_2 = Math.log(2);

    @EngineDependency
    public CameraRepository repository;

    @EngineDependency
    public CoreResourceRepository coreResourceRepository;

    @EngineDependency
    public RuntimeRepository runtimeRepository;

    @Override
    protected void tickInternal() {
        try {
            if (runtimeRepository.inputFocused) {
                handleMouse();
                handleKeyboard();

                Quaternionf pitch = (new Quaternionf()).fromAxisAngleDeg(1f, 0f, 0f, (float) Math.toDegrees(repository.pitch));
                Quaternionf yaw = (new Quaternionf()).fromAxisAngleDeg(0, 1f, 0, (float) Math.toDegrees(repository.yaw));
                repository.currentCamera.rotationBuffer.set(pitch);
                repository.currentCamera.rotationBuffer.mul(yaw);

                repository.toApplyTranslation.rotate(repository.currentCamera.rotationBuffer);
                repository.currentCamera.translationBuffer.add(repository.toApplyTranslation);
            } else {
                repository.firstMouseMove = true;
            }

            updateMatrices();
            updateUBOBuffer();
        } catch (Exception e) {
            getLogger().error("Error processing camera", e);
        }
    }

    private void handleKeyboard() {
        float multiplier = runtimeRepository.fasterPressed ? 10 * repository.movementSpeed : repository.movementSpeed;
        if (runtimeRepository.leftPressed) {
            repository.toApplyTranslation.x -= multiplier;
        }
        if (runtimeRepository.rightPressed) {
            repository.toApplyTranslation.x += multiplier;
        }
        if (runtimeRepository.backwardPressed) {
            if (repository.currentCamera.isOrthographic) {
                repository.currentCamera.orthographicProjectionSize += multiplier;
            } else {
                repository.toApplyTranslation.z += multiplier;
            }
        }
        if (runtimeRepository.forwardPressed) {
            if (repository.currentCamera.isOrthographic) {
                repository.currentCamera.orthographicProjectionSize -= multiplier;
            } else {
                repository.toApplyTranslation.z -= multiplier;
            }
        }
    }

    private void updateMatrices() {

//        float elapsed = clock.elapsed;
//        float tSmoothing = CameraNotificationDecoder.translationSmoothing;
//        float incrementTranslation = tSmoothing == 0 ? 1 : 1 - (float) Math.pow(.001, elapsed * tSmoothing);
//        repository.currentCamera.currentTranslation.lerp(repository.currentCamera.translationBuffer, incrementTranslation);
//        repository.currentCamera.currentRotation.set(repository.currentCamera.rotationBuffer);
        updateView();
        updateProjection();

        repository.toApplyTranslation.x = 0;
        repository.toApplyTranslation.y = 0;
        repository.toApplyTranslation.z = 0;
    }

    public void updateView() {
        final Camera camera = repository.currentCamera;
        camera.invViewMatrix.identity();

        camera.invViewMatrix.rotate(camera.rotationBuffer).translate(camera.translationBuffer);
        camera.invViewMatrix.invert(camera.viewMatrix);
        camera.position.set(camera.invViewMatrix.m30(), camera.invViewMatrix.m31(), camera.invViewMatrix.m32());

        camera.projectionMatrix.get(camera.viewProjectionMatrix).mul(camera.viewMatrix);

        camera.staticViewMatrix.set(camera.viewMatrix);
        camera.staticViewMatrix.m30(0).m31(0).m32(0);
    }

    private void updateProjection() {
        Camera camera = repository.currentCamera;

        if (camera.isOrthographic) {
            camera.projectionMatrix.setOrtho(-camera.orthographicProjectionSize, camera.orthographicProjectionSize,
                    -camera.orthographicProjectionSize / camera.aspectRatio, camera.orthographicProjectionSize / camera.aspectRatio,
                    -camera.zFar, camera.zFar);
        } else {
            camera.projectionMatrix.setPerspective(camera.fov, camera.aspectRatio, camera.zNear, camera.zFar);
            camera.skyboxProjectionMatrix.setPerspective(camera.fov, camera.aspectRatio, 0.1f, 1000f);
            camera.invSkyboxProjectionMatrix.set(camera.skyboxProjectionMatrix).invert();
        }

        camera.invProjectionMatrix.set(camera.projectionMatrix).invert();
        camera.viewProjectionMatrix.set(camera.projectionMatrix).mul(camera.viewMatrix);
    }

    private void handleMouse() {
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

        repository.lastMouseX = mouseX;
        repository.lastMouseY = mouseY;
    }

    private void updateUBOBuffer() {
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
}