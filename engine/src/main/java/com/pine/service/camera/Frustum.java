package com.pine.service.camera;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.Serializable;

public class Frustum implements Serializable {
    private static final int NUM_PLANES = 6;
    public Plane[] planes = new Plane[NUM_PLANES];
    private final Vector3f positiveAux = new Vector3f();

    public Frustum() {
        for (int i = 0; i < NUM_PLANES; i++) {
            planes[i] = new Plane();
        }
    }

    public void extractPlanes(Matrix4f viewProjectionMatrix) {
        Vector4f row0 = new Vector4f();
        Vector4f row1 = new Vector4f();
        Vector4f row2 = new Vector4f();
        Vector4f row3 = new Vector4f();

        // Extract the rows from the matrix
        viewProjectionMatrix.getRow(0, row0);
        viewProjectionMatrix.getRow(1, row1);
        viewProjectionMatrix.getRow(2, row2);
        viewProjectionMatrix.getRow(3, row3);

        // Left plane: (row3 + row0)
        planes[0].normal.set(row3.x + row0.x, row3.y + row0.y, row3.z + row0.z);
        planes[0].distance = row3.w + row0.w;
        planes[0].normalize();

        // Right plane: (row3 - row0)
        planes[1].normal.set(row3.x - row0.x, row3.y - row0.y, row3.z - row0.z);
        planes[1].distance = row3.w - row0.w;
        planes[1].normalize();

        // Bottom plane: (row3 + row1)
        planes[2].normal.set(row3.x + row1.x, row3.y + row1.y, row3.z + row1.z);
        planes[2].distance = row3.w + row1.w;
        planes[2].normalize();

        // Top plane: (row3 - row1)
        planes[3].normal.set(row3.x - row1.x, row3.y - row1.y, row3.z - row1.z);
        planes[3].distance = row3.w - row1.w;
        planes[3].normalize();

        // Near plane: (row3 + row2)
        planes[4].normal.set(row3.x + row2.x, row3.y + row2.y, row3.z + row2.z);
        planes[4].distance = row3.w + row2.w;
        planes[4].normalize();

        // Far plane: (row3 - row2)
        planes[5].normal.set(row3.x - row2.x, row3.y - row2.y, row3.z - row2.z);
        planes[5].distance = row3.w - row2.w;
        planes[5].normalize();
    }

    public boolean isCubeInFrustum(Vector3f min, Vector3f max) {
        for (Plane plane : planes) {
            positiveAux.x = (plane.normal.x >= 0) ? max.x : min.x;
            positiveAux.y = (plane.normal.y >= 0) ? max.y : min.y;
            positiveAux.z = (plane.normal.z >= 0) ? max.z : min.z;

            if (plane.distanceToPoint(positiveAux) < 0) {
                return false;
            }
        }
        return true;
    }
}
