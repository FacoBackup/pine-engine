package com.pine.service.svo;

import com.pine.service.streaming.mesh.MeshStreamData;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;


public class VoxelizerUtil {

    public static void traverseMesh(MeshStreamData rawMeshData, Matrix4f globalMatrix, SparseVoxelOctree octree) {
        int[] indices = rawMeshData.indices();
        int offset = octree.getResolution() / 2;
        float[] vertices = rawMeshData.vertices();

        for (int i = 0; i < indices.length; i += 3) {
            Vector4f v0 = new Vector4f(vertices[indices[i] * 3], vertices[indices[i] * 3 + 1], vertices[indices[i] * 3 + 2], 1).mul(globalMatrix);
            Vector4f v1 = new Vector4f(vertices[indices[i + 1] * 3], vertices[indices[i + 1] * 3 + 1], vertices[indices[i + 1] * 3 + 2], 1).mul(globalMatrix);
            Vector4f v2 = new Vector4f(vertices[indices[i + 2] * 3], vertices[indices[i + 2] * 3 + 1], vertices[indices[i + 2] * 3 + 2], 1).mul(globalMatrix);
            octree.insert(new Vector3f(v0.x + offset, v0.y + offset, v0.z + offset), new VoxelData(1, 0, 1));
            octree.insert(new Vector3f(v1.x + offset, v1.y + offset, v1.z + offset), new VoxelData(1, 0, 1));
            octree.insert(new Vector3f(v2.x + offset, v2.y + offset, v2.z + offset), new VoxelData(1, 0, 1));
        }
    }
}
