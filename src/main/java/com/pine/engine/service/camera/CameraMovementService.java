package com.pine.engine.service.camera;

import com.pine.common.injection.PBean;
import com.pine.common.injection.PInject;
import com.pine.engine.repository.CameraRepository;
import com.pine.engine.repository.ClockRepository;
import com.pine.engine.repository.RuntimeRepository;
import org.joml.Vector3f;

@PBean
public class CameraMovementService {
    public static final float PI_2 = (float) ((float) Math.PI / 2.0);
    private static final float MIN_MAX_PITCH = (float) Math.toRadians(89.0f);
    private final Vector3f toApplyTranslation = new Vector3f(0);
    private final Vector3f xAxis = new Vector3f();
    private final Vector3f yAxis = new Vector3f();
    private final Vector3f zAxis = new Vector3f();

    @PInject
    public CameraRepository cameraRepository;

    @PInject
    public RuntimeRepository runtimeRepository;

    @PInject
    public ClockRepository clockRepository;

    private void handleInputInternal(Camera camera) {
        Vector3f forward = new Vector3f(
                (float) -Math.sin(camera.yaw) * (float) Math.cos(camera.pitch),  // -sin(yaw)
                (float) Math.sin(camera.pitch),                                  // sin(pitch)
                (float) -Math.cos(camera.yaw) * (float) Math.cos(camera.pitch)   // -cos(yaw)
        );
        Vector3f right = new Vector3f(
                (float) Math.sin(camera.yaw - PI_2),
                0,
                (float) Math.cos(camera.yaw - PI_2)
        );
        forward.normalize();
        right.normalize();

        float multiplier = (runtimeRepository.fasterPressed ? 80 : 40) * cameraRepository.movementSensitivity * clockRepository.deltaTime;
        if (runtimeRepository.leftPressed) {
            camera.position.add(right.mul(multiplier));
            camera.registerChange();
        }
        if (runtimeRepository.rightPressed) {
            camera.position.sub(right.mul(multiplier));
            camera.registerChange();
        }
        if (runtimeRepository.backwardPressed) {
            if (camera.isOrthographic) {
                camera.orthographicProjectionSize += multiplier;
            } else {
                camera.position.sub(forward.mul(multiplier));
            }
            camera.registerChange();
        }
        if (runtimeRepository.forwardPressed) {
            if (camera.isOrthographic) {
                camera.orthographicProjectionSize -= multiplier;
            } else {
                camera.position.add(forward.mul(multiplier));
            }
            camera.registerChange();
        }
    }

    public void handleInput(Camera camera, boolean isFirstMovement) {
        handleInputInternal(camera);
        handleMouse(camera, isFirstMovement);
    }

    public void createViewMatrix(Camera camera) {
        float cosPitch = (float) Math.cos(camera.pitch);
        float sinPitch = (float) Math.sin(camera.pitch);
        float cosYaw = (float) Math.cos(camera.yaw);
        float sinYaw = (float) Math.sin(camera.yaw);

        xAxis.set(cosYaw, 0, -sinYaw);
        yAxis.set(sinYaw * sinPitch, cosPitch, cosYaw * sinPitch);
        zAxis.set(sinYaw * cosPitch, -sinPitch, cosPitch * cosYaw);

        cameraRepository.viewMatrix.set(
                xAxis.x, yAxis.x, zAxis.x, 0,
                xAxis.y, yAxis.y, zAxis.y, 0,
                xAxis.z, yAxis.z, zAxis.z, 0,
                -xAxis.dot(camera.position), -yAxis.dot(camera.position), -zAxis.dot(camera.position), 1);
        toApplyTranslation.set(0);
    }

    final protected void handleMouse(Camera camera, boolean isFirstMovement) {
        updateDelta(isFirstMovement);

        camera.yaw -= (float) Math.toRadians(cameraRepository.deltaX);
        camera.pitch += (float) Math.toRadians(cameraRepository.deltaY);
        camera.pitch = Math.max(-MIN_MAX_PITCH, Math.min(MIN_MAX_PITCH, camera.pitch));

        camera.registerChange();
    }

    public void updateDelta(boolean isFirstMovement) {
        float mouseX = runtimeRepository.mouseX;
        float mouseY = runtimeRepository.mouseY;

        if (isFirstMovement) {
            cameraRepository.lastMouseX = mouseX;
            cameraRepository.lastMouseY = mouseY;
        }

        cameraRepository.deltaX = (mouseX - cameraRepository.lastMouseX) * cameraRepository.rotationSensitivity * .25f;
        cameraRepository.deltaY = (cameraRepository.lastMouseY - mouseY) * cameraRepository.rotationSensitivity * .25f;


        cameraRepository.lastMouseX = mouseX;
        cameraRepository.lastMouseY = mouseY;
    }
}
