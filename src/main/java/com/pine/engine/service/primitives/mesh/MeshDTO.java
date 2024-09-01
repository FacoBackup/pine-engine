package com.pine.engine.service.primitives.mesh;

import com.pine.common.resource.IResourceCreationData;
import com.pine.common.resource.ResourceType;
import jakarta.annotation.Nullable;

public record MeshDTO(float[] vertices,
                      int[] indices,
                      @Nullable float[] normals,
                      @Nullable float[] uvs) implements IResourceCreationData {
    @Override
    public ResourceType getResourceType() {
        return ResourceType.MESH;
    }
}
