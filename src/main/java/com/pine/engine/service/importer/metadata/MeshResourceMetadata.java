package com.pine.engine.service.importer.metadata;

import com.pine.common.inspection.InspectableField;
import com.pine.engine.repository.streaming.StreamableResourceType;

public class MeshResourceMetadata extends AbstractResourceMetadata {
    @InspectableField(label = "Number of vertices", disabled = true)
    public final int vertices;

    @InspectableField(label = "Number of triangles", disabled = true)
    public final int triangles;

    @InspectableField(label = "Has Normal information", disabled = true)
    public final boolean hasNormals;

    @InspectableField(label = "Has UV information", disabled = true)
    public final boolean hasUVs;

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
