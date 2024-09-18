package com.pine.service.resource.primitives.mesh;

import com.pine.service.resource.resource.ResourceCreationData;
import com.pine.service.resource.resource.ResourceType;
import jakarta.annotation.Nullable;

public record MeshCreationData(float[] vertices, int[] indices, @Nullable float[] normals,
                               @Nullable float[] uvs) implements ResourceCreationData {

    @Override
    public ResourceType getResourceType() {
        return ResourceType.MESH;
    }
}
