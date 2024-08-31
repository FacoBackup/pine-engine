package com.pine.core.service.repository.primitives.mesh;

import com.pine.core.service.ResourceType;
import com.pine.core.service.common.IResourceCreationData;
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
