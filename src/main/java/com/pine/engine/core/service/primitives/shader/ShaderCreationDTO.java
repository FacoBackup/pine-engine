package com.pine.engine.core.service.primitives.shader;

import com.pine.engine.core.resource.IResourceCreationData;
import com.pine.engine.core.resource.ResourceType;

public record ShaderCreationDTO(String vertex, String fragment, String absoluteId) implements IResourceCreationData {
    @Override
    public ResourceType getResourceType() {
        return ResourceType.SHADER;
    }
}
