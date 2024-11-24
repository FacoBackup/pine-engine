package com.pine.engine.service.voxelization.util;

import com.pine.engine.service.importer.data.MeshImportData;
import com.pine.engine.service.streaming.data.TextureStreamData;
import com.pine.engine.service.voxelization.svo.SparseVoxelOctree;
import org.joml.Vector3f;


public class VoxelizerUtil {

    public static void voxelize(MeshImportData rawMeshData, SparseVoxelOctree octree, TextureStreamData albedoTexture) {
        int[] indices = rawMeshData.indices;
        float[] vertices = rawMeshData.vertices;
        float[] uvs = rawMeshData.uvs;

        for (int i = 0; i < indices.length; i += 3) {
            int index = indices[i];
            int index1 = indices[i + 1];
            int index2 = indices[i + 2];
            var triangle = new Triangle(
                    new Vector3f(vertices[index * 3], vertices[index * 3 + 1], vertices[index * 3 + 2]),
                    new Vector3f(vertices[index1 * 3], vertices[index1 * 3 + 1], vertices[index1 * 3 + 2]),
                    new Vector3f(vertices[index2 * 3], vertices[index2 * 3 + 1], vertices[index2 * 3 + 2])
            );
            if (uvs != null) {
                triangle.uv0.x = uvs[index * 2];
                triangle.uv0.y = uvs[index * 2 + 1];

                triangle.uv1.x = uvs[index1 * 2];
                triangle.uv1.y = uvs[index1 * 2 + 1];

                triangle.uv2.x = uvs[index2 * 2];
                triangle.uv2.y = uvs[index2 * 2 + 1];
            }
            iterateTriangle(triangle, octree, albedoTexture);
        }
    }

    private static void iterateTriangle(Triangle triangle, SparseVoxelOctree octree, TextureStreamData albedoTexture) {
        float edgeLength1 = triangle.v0.distance(triangle.v1);
        float edgeLength2 = triangle.v1.distance(triangle.v2);
        float edgeLength3 = triangle.v2.distance(triangle.v0);

        float maxEdgeLength = Math.max(edgeLength1, Math.max(edgeLength2, edgeLength3));
        float stepSize = octree.getVoxelSize() / maxEdgeLength;

        for (float lambda1 = 0; lambda1 <= 1; lambda1 += stepSize) {
            for (float lambda2 = 0; lambda2 <= 1 - lambda1; lambda2 += stepSize) {
                float lambda0 = 1 - lambda1 - lambda2;

                float u = lambda0 * triangle.uv0.x + lambda1 * triangle.uv1.x + lambda2 * triangle.uv2.x;
                float v = lambda0 * triangle.uv0.y + lambda1 * triangle.uv1.y + lambda2 * triangle.uv2.y;

                octree.insert(
                        new Vector3f(
                                lambda0 * triangle.v0.x + lambda1 * triangle.v1.x + lambda2 * triangle.v2.x,
                                lambda0 * triangle.v0.y + lambda1 * triangle.v1.y + lambda2 * triangle.v2.y,
                                lambda0 * triangle.v0.z + lambda1 * triangle.v1.z + lambda2 * triangle.v2.z
                        ),
                        TextureUtil.sampleTextureAtUV(albedoTexture, u, v)
                );
            }
        }
    }
}
