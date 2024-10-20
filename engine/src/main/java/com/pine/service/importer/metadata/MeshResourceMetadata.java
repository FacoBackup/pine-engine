package com.pine.service.importer.metadata;

import com.pine.repository.streaming.StreamableResourceType;

public class MeshResourceMetadata extends AbstractResourceMetadata {
    public final int vertices, triangles;
    public final boolean hasNormals, hasUVs;

    public MeshResourceMetadata(String name, String id, int vertices, int triangles, boolean hasNormals, boolean hasUVs) {
        super(name, id);
        this.vertices = vertices;
        this.triangles = triangles;
        this.hasUVs = hasUVs;
        this.hasNormals = hasNormals;
    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.MESH;
    }
}
