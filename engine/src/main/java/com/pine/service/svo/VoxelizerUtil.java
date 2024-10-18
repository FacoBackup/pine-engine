package com.pine.service.svo;

import com.pine.service.streaming.mesh.MeshStreamData;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;


public class VoxelizerUtil {

    public static void traverseMesh(MeshStreamData rawMeshData, Matrix4f globalMatrix, SparseVoxelOctree octree) {

        int[] indices = rawMeshData.indices();
        float[] vertices = rawMeshData.vertices();

        for (int i = 0; i < indices.length; i += 3) {
            Vector4f v0 = new Vector4f(vertices[indices[i] * 3], vertices[indices[i] * 3 + 1], vertices[indices[i] * 3 + 2], 1).mul(globalMatrix);
            Vector4f v1 = new Vector4f(vertices[indices[i + 1] * 3], vertices[indices[i + 1] * 3 + 1], vertices[indices[i + 1] * 3 + 2], 1).mul(globalMatrix);
            Vector4f v2 = new Vector4f(vertices[indices[i + 2] * 3], vertices[indices[i + 2] * 3 + 1], vertices[indices[i + 2] * 3 + 2], 1).mul(globalMatrix);

            iterateTriangle(v0, v1, v2, octree);
        }
    }

    private static void iterateTriangle(Vector4f v0, Vector4f v1, Vector4f v2, SparseVoxelOctree octree) {
        Vector3f size = computeTriangleSize(v0, v1, v2);
        float triangleArea = computeSurfaceArea(size.x, size.y, size.z);
        // For triangles smaller than a voxel
        if (triangleArea <= octree.getVoxelSize()) {
            octree.insert(new Vector3f(v0.x + octree.getOffset(), v0.y + octree.getOffset(), v0.z + octree.getOffset()), new VoxelData(1, 0, 1));
            octree.insert(new Vector3f(v1.x + octree.getOffset(), v1.y + octree.getOffset(), v1.z + octree.getOffset()), new VoxelData(1, 0, 1));
            octree.insert(new Vector3f(v2.x + octree.getOffset(), v2.y + octree.getOffset(), v2.z + octree.getOffset()), new VoxelData(1, 0, 1));
            return;
        }

        // Iterate over barycentric coordinates for triangles bigger than a voxel
        for (float lambda1 = 0; lambda1 <= 1; lambda1 += octree.getVoxelizationStepSize()) {
            for (float lambda2 = 0; lambda2 <= 1 - lambda1; lambda2 += octree.getVoxelizationStepSize()) {
                float lambda0 = 1 - lambda1 - lambda2;
                octree.insert(
                        new Vector3f(
                                lambda0 * v0.x + lambda1 * v1.x + lambda2 * v2.x + octree.getOffset(),
                                lambda0 * v0.y + lambda1 * v1.y + lambda2 * v2.y + octree.getOffset(),
                                lambda0 * v0.z + lambda1 * v1.z + lambda2 * v2.z + octree.getOffset()
                        ),
                        new VoxelData(1, 0, 1));
            }
        }
    }

    private static Vector3f computeTriangleSize(Vector4f v0, Vector4f v1, Vector4f v2) {
        Vector3f min = new Vector3f(
                Math.min(v0.x, Math.min(v1.x, v2.x)),
                Math.min(v0.y, Math.min(v1.y, v2.y)),
                Math.min(v0.z, Math.min(v1.z, v2.z))
        );
        Vector3f max = new Vector3f(
                Math.max(v0.x, Math.max(v1.x, v2.x)),
                Math.max(v0.y, Math.max(v1.y, v2.y)),
                Math.max(v0.z, Math.max(v1.z, v2.z))
        );

        return new Vector3f(
                max.x - min.x,
                max.y - min.y,
                max.z - min.z
        );
    }

    public static float computeSurfaceArea(float width, float height, float depth) {
        return 2 * (width * height + height * depth + width * depth);
    }
}
