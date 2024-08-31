package com.pine.engine.service.primitives.shader;

import com.pine.common.resource.ResourceType;
import com.pine.common.resource.IResourceCreationData;

public record ShaderCreationDTO(String vertex, String fragment, String absoluteId) implements IResourceCreationData {
    @Override
    public ResourceType getResourceType() {
        return ResourceType.SHADER;
    }
}
