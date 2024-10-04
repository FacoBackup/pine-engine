package com.pine.service.streaming.mesh;

import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.streaming.StreamLoadData;

import javax.annotation.Nullable;

public final class MeshStreamData implements StreamLoadData {
    private final float[] vertices;
    private final int[] indices;
    @Nullable
    private final float[] normals;
    @Nullable
    private final float[] uvs;

    public MeshStreamData(float[] vertices, int[] indices, @Nullable float[] normals,
                          @Nullable float[] uvs) {
        this.vertices = vertices;
        this.indices = indices;
        this.normals = normals;
        this.uvs = uvs;
    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.MESH;
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
