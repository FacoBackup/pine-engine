package com.pine.service.camera;

import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.CameraRepository;
import com.pine.repository.ClockRepository;
import com.pine.repository.RuntimeRepository;
import org.joml.Vector3f;

@PBean
public class CameraFirstPersonService extends AbstractCameraService {
    private final Vector3f toApplyTranslation = new Vector3f(0);
    private final Vector3f xAxis = new Vector3f();
    private final Vector3f yAxis = new Vector3f();
    private final Vector3f zAxis = new Vector3f();

    @PInject
    public RuntimeRepository runtimeRepository;

    @PInject
    public ClockRepository clockRepository;

    @PInject
    public CameraRepository cameraRepository;

    @Override
    public void handleInputInternal(Camera camera) {
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

        float multiplier = (runtimeRepository.fasterPressed ? 20 : 10) * cameraRepository.movementSpeed * clockRepository.deltaTime;
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

    @Override
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
}
