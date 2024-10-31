package com.pine.service.camera;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.Serializable;

public class Frustum implements Serializable {
    private final Vector4f[] planes = new Vector4f[6];

    public Frustum() {
        for (int i = 0; i < 6; i++) {
            planes[i] = new Vector4f();
        }
    }

    public void extractFrustumPlanes(Matrix4f viewProjectionMatrix) {
        float[] m = new float[16];
        viewProjectionMatrix.get(m);

        // Left plane
        planes[0].set(m[3] + m[0], m[7] + m[4], m[11] + m[8], m[15] + m[12]).normalize();

        // Right plane
        planes[1].set(m[3] - m[0], m[7] - m[4], m[11] - m[8], m[15] - m[12]).normalize();

        // Bottom plane
        planes[2].set(m[3] + m[1], m[7] + m[5], m[11] + m[9], m[15] + m[13]).normalize();

        // Top plane
        planes[3].set(m[3] - m[1], m[7] - m[5], m[11] - m[9], m[15] - m[13]).normalize();

        // Near plane
        planes[4].set(m[3] + m[2], m[7] + m[6], m[11] + m[10], m[15] + m[14]).normalize();

        // Far plane
        planes[5].set(m[3] - m[2], m[7] - m[6], m[11] - m[10], m[15] - m[14]).normalize();
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
}
