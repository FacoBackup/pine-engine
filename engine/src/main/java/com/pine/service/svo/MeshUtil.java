package com.pine.service.svo;

import com.pine.service.importer.data.MeshImportData;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class MeshUtil {
    public static BoundingBox computeBoundingBox(MeshImportData mesh) {
        var bounding = new BoundingBox();
        int[] indices = mesh.indices;
        float[] vertices = mesh.vertices;

        for (int i = 0; i < indices.length; i++) {
            int index = indices[i];
            Vector3f vertex = new Vector3f(vertices[index * 3], vertices[index * 3 + 1], vertices[index * 3 + 2]);

            if (vertex.x < bounding.min.x) bounding.min.x = vertex.x;
            if (vertex.y < bounding.min.y) bounding.min.y = vertex.y;
            if (vertex.z < bounding.min.z) bounding.min.z = vertex.z;

            if (vertex.x > bounding.max.x) bounding.max.x = vertex.x;
            if (vertex.y > bounding.max.y) bounding.max.y = vertex.y;
            if (vertex.z > bounding.max.z) bounding.max.z = vertex.z;
        }
        return bounding;
    }

    public static MeshImportData transformVertices(MeshImportData stream, Matrix4f transform) {
        int[] indices = stream.indices;
        float[] vertices = stream.vertices;

        float[] verticesNew = new float[vertices.length];

        for (int i = 0; i < indices.length; i += 3) {
            Vector4f v0 = new Vector4f(vertices[indices[i] * 3], vertices[indices[i] * 3 + 1], vertices[indices[i] * 3 + 2], 1).mul(transform);
            Vector4f v1 = new Vector4f(vertices[indices[i + 1] * 3], vertices[indices[i + 1] * 3 + 1], vertices[indices[i + 1] * 3 + 2], 1).mul(transform);
            Vector4f v2 = new Vector4f(vertices[indices[i + 2] * 3], vertices[indices[i + 2] * 3 + 1], vertices[indices[i + 2] * 3 + 2], 1).mul(transform);

            verticesNew[indices[i] * 3] = v0.x;
            verticesNew[indices[i] * 3 + 1] = v0.y;
            verticesNew[indices[i] * 3 + 2] = v0.z;

            verticesNew[indices[i + 1] * 3] = v1.x;
            verticesNew[indices[i + 1] * 3 + 1] = v1.y;
            verticesNew[indices[i + 1] * 3 + 2] = v1.z;

            verticesNew[indices[i + 2] * 3] = v2.x;
            verticesNew[indices[i + 2] * 3 + 1] = v2.y;
            verticesNew[indices[i + 2] * 3 + 2] = v2.z;
        }

        return new MeshImportData(null, verticesNew, indices, null, stream.uvs);
    }
}
