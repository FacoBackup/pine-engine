package com.pine.engine.core.service.resource.primitives.mesh;

import com.pine.engine.core.service.resource.resource.ResourceCreationData;
import com.pine.engine.core.service.resource.resource.ResourceType;
import jakarta.annotation.Nullable;

import java.util.Objects;

public record MeshCreationData(float[] vertices, int[] indices, @Nullable float[] normals,
                               @Nullable float[] uvs) implements ResourceCreationData {

    @Override
    public ResourceType getResourceType() {
        return ResourceType.MESH;
    }
}
