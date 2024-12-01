package com.pine.engine.service.camera;

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

    public void extractFrustumPlanes(Matrix4f m) {
        for (int i = 0; i < planes.length; i++) {
            m.frustumPlane(i, planes[i]);
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
}
