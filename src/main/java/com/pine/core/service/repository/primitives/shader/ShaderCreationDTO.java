package com.pine.core.service.repository.primitives.shader;

import com.pine.core.service.ResourceType;
import com.pine.core.service.common.IResourceCreationData;

public record ShaderCreationDTO(String vertex, String fragment) implements IResourceCreationData {
    @Override
    public ResourceType getResourceType() {
        return ResourceType.SHADER;
    }
}
