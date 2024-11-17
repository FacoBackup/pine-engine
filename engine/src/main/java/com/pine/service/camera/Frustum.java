package com.pine.service.camera;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.Serializable;

public class Frustum implements Serializable {
    private final Vector4f[] planes = new Vector4f[6];
    private final Matrix4f transpose = new Matrix4f();

    public Frustum() {
        for (int i = 0; i < 6; i++) {
            planes[i] = new Vector4f();
        }
    }

    public void extractFrustumPlanes(Matrix4f viewProjectionMatrix) {
        transpose.set(viewProjectionMatrix);
        for (int i = 0; i < planes.length; i++) {
            transpose.frustumPlane(i, planes[i]);
        }
    }

    public boolean isSphereInsideFrustum(Vector3f center, float radius) {
        for (Vector4f plane : planes) {
            float distance = plane.x * center.x + plane.y * center.y + plane.z * center.z + plane.w;
            if (distance < -radius) {
                return false;
            }
        }
        return true;
    }

    public boolean isPointInsideFrustum(Vector3f center) {
        Vector4f p4D = new Vector4f(center, 1.0f);
        return (planes[0].dot(p4D) >= 0) &&
                (planes[1].dot(p4D) <= 0) &&
                (planes[2].dot(p4D) >= 0) &&
                (planes[3].dot(p4D) <= 0) &&
                (planes[4].dot(p4D) >= 0) &&
                (planes[5].dot(p4D) <= 0);
    }
}
