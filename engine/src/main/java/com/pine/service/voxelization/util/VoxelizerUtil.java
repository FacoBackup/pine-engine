package com.pine.service.voxelization.util;

import com.pine.service.importer.data.MeshImportData;
import com.pine.service.streaming.data.TextureStreamData;
import com.pine.service.voxelization.SparseVoxelOctree;
import com.pine.service.voxelization.Triangle;
import com.pine.service.voxelization.VoxelData;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class VoxelizerUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(VoxelizerUtil.class);

    public static void voxelize(MeshImportData rawMeshData, SparseVoxelOctree octree, float stepSize, TextureStreamData albedoTexture) {
        int[] indices = rawMeshData.indices;
        float[] vertices = rawMeshData.vertices;
        float[] uvs = rawMeshData.uvs;

        for (int i = 0; i < indices.length; i += 3) {
            long start = System.currentTimeMillis();
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
            iterateTriangle(triangle, octree, stepSize, albedoTexture);
            LOGGER.warn("Triangle ({} out of {}) voxelization took {}ms", i, indices.length / 3, System.currentTimeMillis() - start);
        }
    }

    private static void iterateTriangle(Triangle triangle, SparseVoxelOctree octree, float stepSize, TextureStreamData albedoTexture) {
        Vector3f size = computeTriangleSize(triangle);
        float triangleArea = computeSurfaceArea(size.x, size.y, size.z);

        // For triangles smaller than a voxel
        if (triangleArea <= octree.getVoxelSize()) {
            VoxelData color = new VoxelData(0, 0, 0);
            if (albedoTexture != null) {
                color = TextureUtil.sampleTextureAtUV(albedoTexture, triangle.uv0.x, triangle.uv0.y);
            }
            octree.insert(new Vector3f(triangle.v0.x, triangle.v0.y, triangle.v0.z), color);
            octree.insert(new Vector3f(triangle.v1.x, triangle.v1.y, triangle.v1.z), color);
            octree.insert(new Vector3f(triangle.v2.x, triangle.v2.y, triangle.v2.z), color);
        } else {

            // Iterate over barycentric coordinates for triangles bigger than a voxel
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

    private static Vector3f computeTriangleSize(Triangle triangle) {
        Vector3f min = new Vector3f(
                Math.min(triangle.v0.x, Math.min(triangle.v1.x, triangle.v2.x)),
                Math.min(triangle.v0.y, Math.min(triangle.v1.y, triangle.v2.y)),
                Math.min(triangle.v0.z, Math.min(triangle.v1.z, triangle.v2.z))
        );
        Vector3f max = new Vector3f(
                Math.max(triangle.v0.x, Math.max(triangle.v1.x, triangle.v2.x)),
                Math.max(triangle.v0.y, Math.max(triangle.v1.y, triangle.v2.y)),
                Math.max(triangle.v0.z, Math.max(triangle.v1.z, triangle.v2.z))
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
