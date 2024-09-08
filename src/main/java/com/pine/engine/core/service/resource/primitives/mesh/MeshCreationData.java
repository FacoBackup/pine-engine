package com.pine.engine.core.service.resource.primitives.mesh;

import com.pine.engine.core.service.resource.resource.ResourceCreationData;
import com.pine.engine.core.service.resource.resource.ResourceType;
import jakarta.annotation.Nullable;

import java.util.Objects;

public final class MeshCreationData extends ResourceCreationData {
    private final float[] vertices;
    private final int[] indices;
    @Nullable
    private final float[] normals;
    @Nullable
    private final float[] uvs;

    public MeshCreationData(float[] vertices,
                            int[] indices,
                            @Nullable float[] normals,
                            @Nullable float[] uvs) {
        super();
        this.vertices = vertices;
        this.indices = indices;
        this.normals = normals;
        this.uvs = uvs;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.MESH;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (MeshCreationData) obj;
        return Objects.equals(this.vertices, that.vertices) &&
                Objects.equals(this.indices, that.indices) &&
                Objects.equals(this.normals, that.normals) &&
                Objects.equals(this.uvs, that.uvs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vertices, indices, normals, uvs);
    }

    @Override
    public String toString() {
        return "MeshDTO[" +
                "vertices=" + vertices + ", " +
                "indices=" + indices + ", " +
                "normals=" + normals + ", " +
                "uvs=" + uvs + ']';
    }

}
