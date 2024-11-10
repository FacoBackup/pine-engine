package com.pine.service.importer.data;

import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.streaming.data.StreamData;

public class MeshImportData extends AbstractImportData implements StreamData {
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
