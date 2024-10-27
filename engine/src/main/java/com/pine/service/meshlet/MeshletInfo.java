package com.pine.service.meshlet;

public class MeshletInfo {
    public final int vertexIndexStart;
    public final int vertexIndexLength;
    public final int trianglesStart;
    public final int trianglesLength;

    public MeshletInfo(
            int vertexIndexStart,
            int vertexIndexLength,
            int trianglesStart,
            int trianglesLength
    ) {
        this.vertexIndexStart = vertexIndexStart;
        this.vertexIndexLength = vertexIndexLength;
        this.trianglesStart = trianglesStart;
        this.trianglesLength = trianglesLength;
    }
}