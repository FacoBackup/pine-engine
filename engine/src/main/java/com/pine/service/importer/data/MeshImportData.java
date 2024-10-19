package com.pine.service.importer.data;

import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.streaming.StreamData;

import java.io.Serial;

public class MeshImportData extends AbstractImportData implements StreamData {
    @Serial
    private static final long serialVersionUID = -5390350102505027262L;

    public final float[] vertices;
    public final int[] indices;
    public final float[] normals;
    public final float[] uvs;

    public MeshImportData(String name, float[] vertices, int[] indices, float[] normals, float[] uvs) {
        super(name);
        this.vertices = vertices;
        this.indices = indices;
        this.normals = normals;
        this.uvs = uvs;
    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.MESH;
    }
}
