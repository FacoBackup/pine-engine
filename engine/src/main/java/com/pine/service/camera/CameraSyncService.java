package com.pine.service.camera;

import com.pine.EngineUtils;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.CameraRepository;
import com.pine.repository.RuntimeRepository;
import com.pine.repository.core.CoreUBORepository;
import com.pine.tasks.SyncTask;

@PBean
public class CameraSyncService implements SyncTask {
    private static final double LOG_2 = Math.log(2);

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

    private AbstractCameraService cameraService;
    private Camera camera;

    @Override
    public void sync() {
        camera = repository.currentCamera;
        if (camera == null) {
            return;
        }

        if (camera.orbitalMode) {
            cameraService = cameraThirdPersonService;
        } else {
            cameraService = cameraFirstPersonService;
        }
        updateAspectRatio();
        if (camera.isNotFrozen()) {
            updateMatrices();
            updateUBOBuffer();
            camera.freezeVersion();
        }
    }

    private void updateAspectRatio() {
        float prevAspect = camera.aspectRatio;
        camera.aspectRatio = runtimeRepository.viewportW / runtimeRepository.viewportH;
        if(prevAspect != camera.aspectRatio) {
            camera.registerChange();
        }
    }

    private void updateMatrices() {
        updateProjection();
        updateView();
        repository.viewProjectionMatrix.set(repository.projectionMatrix).mul(repository.viewMatrix);
        repository.frustum.extractPlanes(repository.viewProjectionMatrix);
    }

    private void updateView() {
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

        V.put(84, runtimeRepository.getDisplayW());
        V.put(85, runtimeRepository.getDisplayH());
        V.put(86, (float) (2.0 / (Math.log(repository.projectionMatrix.get(0, 0) + 1) / LOG_2)));
    }
}