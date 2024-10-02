package com.pine.service;

import com.pine.PBean;
import com.pine.PInject;
import com.pine.repository.CameraRepository;
import com.pine.repository.RuntimeRepository;
import com.pine.service.camera.Camera;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@PBean
public class CameraThirdPersonService implements CameraService {

    @PInject
    public CameraRepository cameraRepository;

    private final Quaternionf rotationBuffer = new Quaternionf();
    private final Quaternionf pitchQ = new Quaternionf();

    @Override
    public void handleKeyboard(Camera camera) {
    }

    public void changeCenter(Camera camera) {
        camera.orbitCenter.x = cameraRepository.deltaX;
        camera.orbitCenter.y -= cameraRepository.deltaY;
        camera.registerChange();
    }

    public void zoom(Camera camera, float amount) {
        camera.orbitRadius -= amount * cameraRepository.zoomSensitivity;
        if (camera.orbitRadius < 1.0f) camera.orbitRadius = 1.0f;
        if (camera.orbitRadius > 100.0f) camera.orbitRadius = 100.0f;
        camera.registerChange();
    }

    private static Matrix4f arcBall(Vector3f t0, Quaternionf r, Vector3f t1) {
        // Create translation matrix T0 to move the camera away from the object
        Matrix4f T0 = new Matrix4f().translation(-t0.x, -t0.y, -t0.z);

        // Create rotation matrix R from the inverse of quaternion r
        Matrix4f R = new Matrix4f().identity().rotate(r.invert());

        // Create translation matrix T1 to move the camera to the center of the object
        Matrix4f T1 = new Matrix4f().translation(-t1.x, -t1.y, -t1.z);

        // Combine the transformations: T0 * R * T1
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.mul(T0).mul(R).mul(T1);

        return viewMatrix;
    }

    @Override
    public void createViewMatrix(Camera camera) {
        pitchQ.identity().rotateX((float) Math.toRadians(camera.pitch));
        rotationBuffer.identity().rotateY((float) Math.toRadians(camera.yaw)).mul(pitchQ);

        cameraRepository.invViewMatrix.identity().translate(camera.orbitCenter).rotate(rotationBuffer);
        cameraRepository.invViewMatrix.invert(cameraRepository.viewMatrix);
    }
}
