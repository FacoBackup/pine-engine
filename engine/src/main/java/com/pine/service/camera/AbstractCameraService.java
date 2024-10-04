package com.pine.service.camera;

import com.pine.injection.PInject;
import com.pine.repository.CameraRepository;
import com.pine.repository.RuntimeRepository;

public abstract class AbstractCameraService {
    private static final float MIN_MAX_PITCH = (float) Math.toRadians(89.0f);

    @PInject
    public CameraRepository cameraRepository;

    @PInject
    public RuntimeRepository runtimeRepository;

    protected void handleInputInternal(Camera camera) {
    }

    public void handleInput(Camera camera, boolean isFirstMovement) {
        handleInputInternal(camera);
        handleMouse(camera, isFirstMovement);
    }

    public abstract void createViewMatrix(Camera camera);

    final protected void handleMouse(Camera camera, boolean isFirstMovement) {
        float mouseX = runtimeRepository.mouseX;
        float mouseY = runtimeRepository.mouseY;

        if (isFirstMovement) {
            cameraRepository.lastMouseX = mouseX;
            cameraRepository.lastMouseY = mouseY;
        }

        cameraRepository.deltaX = (mouseX - cameraRepository.lastMouseX) * cameraRepository.sensitivity * .25f;
        cameraRepository.deltaY = (cameraRepository.lastMouseY - mouseY) * cameraRepository.sensitivity * .25f;

        camera.yaw -= (float) Math.toRadians(cameraRepository.deltaX);
        camera.pitch += (float) Math.toRadians(cameraRepository.deltaY);
        camera.pitch = Math.max(-MIN_MAX_PITCH, Math.min(MIN_MAX_PITCH, camera.pitch));

        cameraRepository.lastMouseX = mouseX;
        cameraRepository.lastMouseY = mouseY;
        camera.registerChange();
    }
}
