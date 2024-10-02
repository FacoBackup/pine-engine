package com.pine.tasks;

import com.pine.EngineUtils;
import com.pine.Loggable;
import com.pine.PBean;
import com.pine.PInject;
import com.pine.repository.CameraRepository;
import com.pine.repository.CoreUBORepository;
import com.pine.repository.RuntimeRepository;
import com.pine.service.CameraFirstPersonService;
import com.pine.service.CameraService;
import com.pine.service.CameraThirdPersonService;
import com.pine.service.camera.Camera;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@PBean
public class CameraTask extends AbstractTask implements Loggable {
    private static final double LOG_2 = Math.log(2);
    private static final float MIN_MAX_PITCH = (float) Math.toRadians(89.0f);

    @PInject
    public CameraRepository repository;

    @PInject
    public CoreUBORepository uboRepository;

    @PInject
    public RuntimeRepository runtimeRepository;

    @PInject
    public CameraFirstPersonService cameraFirstPersonService;

    @PInject
    public CameraThirdPersonService cameraThirdPersonService;

    private CameraService cameraService;
    private Camera camera;

    @Override
    protected void tickInternal() {
        try {
            camera = repository.currentCamera;

            if (camera.orbitalMode) {
                cameraService = cameraThirdPersonService;
            } else {
                cameraService = cameraFirstPersonService;
            }

            if (runtimeRepository.inputFocused) {
                handleMouse();
                cameraService.handleKeyboard(camera);
            } else {
                repository.firstMouseMove = true;
            }

            if (!camera.isFrozen()) {
                updateMatrices();
                updateUBOBuffer();
                camera.freezeVersion();
            }
        } catch (Exception e) {
            getLogger().error("Error processing camera", e);
        }
    }

    private void handleMouse() {
        float mouseX = runtimeRepository.mouseX;
        float mouseY = runtimeRepository.mouseY;

        if (repository.firstMouseMove) {
            repository.lastMouseX = mouseX;
            repository.lastMouseY = mouseY;
            repository.firstMouseMove = false;
        }

        if (mouseX != repository.lastMouseX && mouseY != repository.lastMouseY) {
            repository.deltaX = (mouseX - repository.lastMouseX) * repository.sensitivity;
            repository.deltaY = (repository.lastMouseY - mouseY) * repository.sensitivity;

            camera.yaw -= (float) Math.toRadians(repository.deltaX);
            camera.pitch += (float) Math.toRadians(repository.deltaY);
            camera.pitch = Math.max(-MIN_MAX_PITCH, Math.min(MIN_MAX_PITCH, camera.pitch));

            repository.lastMouseX = mouseX;
            repository.lastMouseY = mouseY;
            camera.registerChange();
        }
    }

    private void updateMatrices() {
        updateProjection();
        updateView();
        repository.viewProjectionMatrix.set(repository.projectionMatrix).mul(repository.viewMatrix);
        repository.frustum.extractPlanes(repository.viewProjectionMatrix);
    }

    public void updateView() {
        cameraService.createViewMatrix(camera);
        repository.viewMatrix.invert(repository.invViewMatrix);
        repository.staticViewMatrix.set(repository.viewMatrix);
        repository.staticViewMatrix.m30(0).m31(0).m32(0);
    }

    private void updateProjection() {
        camera.aspectRatio = runtimeRepository.viewportW / runtimeRepository.viewportH;
        if (camera.isOrthographic) {
            repository.projectionMatrix.setOrtho(-camera.orthographicProjectionSize, camera.orthographicProjectionSize,
                    -camera.orthographicProjectionSize / camera.aspectRatio, camera.orthographicProjectionSize / camera.aspectRatio,
                    -camera.zFar, camera.zFar);
        } else {
            repository.projectionMatrix.setPerspective(camera.fov, camera.aspectRatio, camera.zNear, camera.zFar);
        }
        repository.skyboxProjectionMatrix.setPerspective(camera.fov, camera.aspectRatio, 0.1f, 1000f);
        repository.invSkyboxProjectionMatrix.set(repository.skyboxProjectionMatrix).invert();
        repository.invProjectionMatrix.set(repository.projectionMatrix).invert();
    }

    private void updateUBOBuffer() {
        var V = uboRepository.cameraViewUBOState;
        EngineUtils.copyWithOffset(V, repository.viewProjectionMatrix, 0);
        EngineUtils.copyWithOffset(V, repository.viewMatrix, 16);
        EngineUtils.copyWithOffset(V, repository.invViewMatrix, 32);
        EngineUtils.copyWithOffset(V, camera.position, 48);
        EngineUtils.copyWithOffset(V, repository.projectionMatrix, 52);
        EngineUtils.copyWithOffset(V, repository.invProjectionMatrix, 68);

        V.put(84, runtimeRepository.viewportW);
        V.put(85, runtimeRepository.viewportH);
        V.put(86, (float) (2.0 / (Math.log(repository.projectionMatrix.get(0, 0) + 1) / LOG_2)));
    }
}