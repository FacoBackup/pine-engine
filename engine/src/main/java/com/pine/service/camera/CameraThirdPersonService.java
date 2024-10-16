package com.pine.service.camera;

import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.CameraRepository;
import com.pine.repository.ClockRepository;
import org.joml.Vector3f;

@PBean
public class CameraThirdPersonService extends AbstractCameraService {

    @PInject
    public CameraRepository cameraRepository;

    public boolean isChangingCenter = false;

    @Override
    public void handleInput(Camera camera, boolean isFirstMovement) {
        if (!isChangingCenter) {
            super.handleInput(camera, isFirstMovement);
        }
    }

    public void changeCenter(Camera camera, boolean isFirstMovement) {
        updateDelta(isFirstMovement);

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

        if (cameraRepository.lastMouseY > 0) {
            camera.orbitCenter.add(right.mul(cameraRepository.deltaY * .05f));
            camera.registerChange();
        } else {
            camera.orbitCenter.sub(right.mul(cameraRepository.deltaY * .05f));
            camera.registerChange();
        }
        if (cameraRepository.lastMouseX > 0) {
            camera.orbitCenter.sub(forward.mul(cameraRepository.deltaX * .05f));
            camera.registerChange();
        } else {
            camera.orbitCenter.add(forward.mul(cameraRepository.deltaX * .05f));
            camera.registerChange();
        }
        camera.orbitCenter.y = 0;
    }

    public void zoom(Camera camera, float amount) {
        camera.orbitRadius -= (amount * .1f) * (cameraRepository.zoomSensitivity + 5);
        if (camera.orbitRadius < 1.0f) camera.orbitRadius = 1.0f;
        if (camera.orbitRadius > 100.0f) camera.orbitRadius = 100.0f;
        camera.registerChange();
    }

    @Override
    public void createViewMatrix(Camera camera) {
        float cosPitch = (float) Math.cos(-camera.pitch);
        camera.position.x = (float) (camera.orbitRadius * cosPitch * Math.cos(-camera.yaw) + camera.orbitCenter.x);
        camera.position.y = (float) (camera.orbitRadius * Math.sin(-camera.pitch) + camera.orbitCenter.y);
        camera.position.z = (float) (camera.orbitRadius * cosPitch * Math.sin(-camera.yaw) + camera.orbitCenter.z);
        lookAt(camera.position, camera.orbitCenter);
    }

    private void lookAt(Vector3f eye, Vector3f center) {
        Vector3f zAxis = new Vector3f(eye).sub(center).normalize();
        Vector3f xAxis = new Vector3f(0, 1, 0).cross(zAxis).normalize();
        Vector3f yAxis = new Vector3f(zAxis).cross(xAxis).normalize();
        cameraRepository.viewMatrix.identity();
        cameraRepository.viewMatrix.m00(xAxis.x);
        cameraRepository.viewMatrix.m10(xAxis.y);
        cameraRepository.viewMatrix.m20(xAxis.z);
        cameraRepository.viewMatrix.m01(yAxis.x);
        cameraRepository.viewMatrix.m11(yAxis.y);
        cameraRepository.viewMatrix.m21(yAxis.z);
        cameraRepository.viewMatrix.m02(zAxis.x);
        cameraRepository.viewMatrix.m12(zAxis.y);
        cameraRepository.viewMatrix.m22(zAxis.z);
        cameraRepository.viewMatrix.m30(-xAxis.dot(eye));
        cameraRepository.viewMatrix.m31(-yAxis.dot(eye));
        cameraRepository.viewMatrix.m32(-zAxis.dot(eye));
    }
}
