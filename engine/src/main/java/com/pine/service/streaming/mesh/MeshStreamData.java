package com.pine.service.streaming.mesh;

import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.importer.data.MeshImportData;
import com.pine.service.streaming.StreamData;

import javax.annotation.Nullable;

public final class MeshStreamData extends MeshImportData implements StreamData {
    public MeshStreamData(float[] vertices, int[] indices, float[] normals, float[] uvs) {
        super(null, vertices, indices, normals, uvs);
    }

    public float[] vertices() {
        return vertices;
    }

    public int[] indices() {
        return indices;
    }

    @Nullable
    public float[] normals() {
        return normals;
    }

    @Nullable
    public float[] uvs() {
        return uvs;
    }
}
