package com.pine.engine.core.service.resource.shader;

import com.pine.engine.core.service.resource.resource.ResourceCreationData;
import com.pine.engine.core.service.resource.resource.ResourceType;

public record ShaderCreationData(String vertex, String fragment, String absoluteId) implements ResourceCreationData {

    @Override
    public ResourceType getResourceType() {
        return ResourceType.SHADER;
    }

}
