package com.pine.tasks;

import com.pine.EngineUtils;
import com.pine.Loggable;
import com.pine.PBean;
import com.pine.PInject;
import com.pine.repository.CameraRepository;
import com.pine.repository.CoreResourceRepository;
import com.pine.repository.RuntimeRepository;
import com.pine.service.camera.Camera;
import org.joml.Quaternionf;

@PBean
public class CameraTask extends AbstractTask implements Loggable {
    private static final double LOG_2 = Math.log(2);

    @PInject
    public CameraRepository repository;

    @PInject
    public CoreResourceRepository coreResourceRepository;

    @PInject
    public RuntimeRepository runtimeRepository;

    @Override
    protected void tickInternal() {
        try {
            if (runtimeRepository.inputFocused) {
                handleMouse();
                handleKeyboard();
                rotateCamera();
            } else {
                repository.firstMouseMove = true;
            }

            updateMatrices();
            updateUBOBuffer();
        } catch (Exception e) {
            getLogger().error("Error processing camera", e);
        }
    }

    private void rotateCamera() {
        Quaternionf camRot = repository.currentCamera.rotationBuffer;

        repository.pitchQ.identity().rotateX((float) Math.toRadians(repository.pitch));
        camRot.identity().rotateY((float) Math.toRadians(repository.yaw)).mul(repository.pitchQ);
        repository.currentCamera.translationBuffer.add(repository.toApplyTranslation);
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
        final Camera camera = repository.currentCamera;

//        float elapsed = clock.elapsed;
//        float tSmoothing = CameraNotificationDecoder.translationSmoothing;
//        float incrementTranslation = tSmoothing == 0 ? 1 : 1 - (float) Math.pow(.001, elapsed * tSmoothing);
//        repository.currentCamera.currentTranslation.lerp(repository.currentCamera.translationBuffer, incrementTranslation);
//        repository.currentCamera.currentRotation.set(repository.currentCamera.rotationBuffer);
        updateProjection();
        updateView();
        camera.viewProjectionMatrix.set(camera.projectionMatrix).mul(camera.viewMatrix);

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

        camera.staticViewMatrix.set(camera.viewMatrix);
        camera.staticViewMatrix.m30(0).m31(0).m32(0);
    }

    private void updateProjection() {
        Camera camera = repository.currentCamera;

        camera.aspectRatio = runtimeRepository.viewportW / runtimeRepository.viewportH;
        if (camera.isOrthographic) {
            camera.projectionMatrix.setOrtho(-camera.orthographicProjectionSize, camera.orthographicProjectionSize,
                    -camera.orthographicProjectionSize / camera.aspectRatio, camera.orthographicProjectionSize / camera.aspectRatio,
                    -camera.zFar, camera.zFar);
        } else {
            camera.projectionMatrix.setPerspective(camera.fov, camera.aspectRatio, camera.zNear, camera.zFar);
        }
        camera.skyboxProjectionMatrix.setPerspective(camera.fov, camera.aspectRatio, 0.1f, 1000f);
        camera.invSkyboxProjectionMatrix.set(camera.skyboxProjectionMatrix).invert();
        camera.invProjectionMatrix.set(camera.projectionMatrix).invert();
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
        repository.pitch += deltaY;
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