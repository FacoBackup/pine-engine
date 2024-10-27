package com.pine.service.meshlet;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

public class Meshlets {
    public final List<MeshletInfo> meshletInfos;
    public final FloatBuffer vertices;
    public final IntBuffer meshletVertices;
    public final ByteBuffer meshletTriangles;

    public Meshlets(List<MeshletInfo> meshletInfos, FloatBuffer vertices, IntBuffer meshletVertices, ByteBuffer meshletTriangles) {
        this.meshletInfos = meshletInfos;
        this.meshletVertices = meshletVertices;
        this.vertices = vertices;
        this.meshletTriangles = meshletTriangles;
    }
}
