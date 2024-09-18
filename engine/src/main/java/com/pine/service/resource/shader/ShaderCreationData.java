package com.pine.service.resource.shader;

import com.pine.service.resource.resource.ResourceCreationData;
import com.pine.service.resource.resource.ResourceType;

public record ShaderCreationData(String vertex, String fragment, String absoluteId) implements ResourceCreationData {

    @Override
    public ResourceType getResourceType() {
        return ResourceType.SHADER;
    }

}
