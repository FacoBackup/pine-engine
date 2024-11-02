package com.pine.service.meshlet;

import com.pine.service.importer.data.MeshImportData;

public class TerrainGenerationUtil {

    public static MeshImportData computeMesh(int size) {
        int vertexCount = (size + 1) * (size + 1);
        float[] vertices = new float[vertexCount * 3]; // x, y, z
        float halfSize = size / 2.0f;

        int index = 0;
        for (int z = 0; z <= size; z++) {
            for (int x = 0; x <= size; x++) {
                vertices[index++] = x - halfSize;
                vertices[index++] = 0.0f;
                vertices[index++] = z - halfSize;
            }
        }
        return new MeshImportData(null, vertices, computeIndices(size), null, null);
    }

    private static int[] computeIndices(int size) {
        int quadCount = size * size;
        int[] indices = new int[quadCount * 6]; // 2 triangles per quad, 3 indices each

        int index = 0;
        for (int z = 0; z < size; z++) {
            for (int x = 0; x < size; x++) {
                int topLeft = z * (size + 1) + x;
                int topRight = topLeft + 1;
                int bottomLeft = (z + 1) * (size + 1) + x;
                int bottomRight = bottomLeft + 1;

                // Triangle 1
                indices[index++] = topLeft;
                indices[index++] = bottomLeft;
                indices[index++] = topRight;

                // Triangle 2
                indices[index++] = topRight;
                indices[index++] = bottomLeft;
                indices[index++] = bottomRight;
            }
        }
        return indices;
    }

}
