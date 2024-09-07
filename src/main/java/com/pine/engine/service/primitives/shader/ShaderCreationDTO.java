package com.pine.engine.service.primitives.shader;

import com.pine.engine.resource.IResourceCreationData;
import com.pine.engine.resource.ResourceType;

public record ShaderCreationDTO(String vertex, String fragment, String absoluteId) implements IResourceCreationData {
    @Override
    public ResourceType getResourceType() {
        return ResourceType.SHADER;
    }
}
