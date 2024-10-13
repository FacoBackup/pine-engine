package com.pine.service.importer.impl.mesh;

public record CoreMeshData(
        float[] vertices,
        int[] indices,
        float[] normals,
        float[] uvs) {
}
