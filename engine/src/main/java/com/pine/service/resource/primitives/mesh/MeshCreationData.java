package com.pine.service.resource.primitives.mesh;

import com.pine.service.resource.resource.ResourceCreationData;
import com.pine.service.resource.resource.ResourceType;

import javax.annotation.Nullable;

public final class MeshCreationData extends ResourceCreationData {
    private final float[] vertices;
    private final int[] indices;
    @Nullable
    private final float[] normals;
    @Nullable
    private final float[] uvs;

    public MeshCreationData(float[] vertices, int[] indices, @Nullable float[] normals,
                            @Nullable float[] uvs) {
        this.vertices = vertices;
        this.indices = indices;
        this.normals = normals;
        this.uvs = uvs;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.PRIMITIVE;
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
