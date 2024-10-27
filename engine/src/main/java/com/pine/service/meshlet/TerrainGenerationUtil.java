package com.pine.service.meshlet;

import com.pine.service.importer.data.MeshImportData;

public class TerrainGenerationUtil {

    public static MeshImportData computeMesh(int vertexCount, float dimension, float offset) {
        int count = (int) Math.pow(vertexCount, 2);
        int vertexPointer = 0;
        float[] vertices = new float[count * 3];
        float[] uvs = new float[count * 2];
        for (int i = 0; i < vertexCount; i++) {
            for (int j = 0; j < vertexCount; j++) {
                vertices[vertexPointer * 3] = ((float) j / (vertexCount - 1)) * dimension - offset;
                vertices[vertexPointer * 3 + 1] = 0;
                vertices[vertexPointer * 3 + 2] = ((float) i / (vertexCount - 1)) * dimension - offset;

                uvs[vertexPointer * 2] = (float) j / (vertexCount - 1);
                uvs[vertexPointer * 2 + 1] = (float) i / (vertexCount - 1);
                vertexPointer++;
            }
        }
        return new MeshImportData(null, vertices, computeIndices(vertexCount), null, uvs);
    }

    private static int[] computeIndices(int vertexCount) {
        int pointer = 0;
        int[] indices = new int[6 * (vertexCount - 1) * vertexCount];
        for (int gz = 0; gz < vertexCount - 1; gz++) {
            for (int gx = 0; gx < vertexCount - 1; gx++) {
                int topLeft = (gz * vertexCount) + gx,
                        topRight = topLeft + 1,
                        bottomLeft = ((gz + 1) * vertexCount) + gx,
                        bottomRight = bottomLeft + 1;


                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }
        return indices;
    }

}
