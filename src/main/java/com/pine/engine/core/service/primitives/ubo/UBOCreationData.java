package com.pine.engine.core.service.primitives.ubo;

import com.pine.engine.core.resource.IResourceCreationData;
import com.pine.engine.core.resource.ResourceType;

import java.util.List;

public record UBOCreationData(List<UBOData> data, String blockName) implements IResourceCreationData {
    @Override
    public ResourceType getResourceType() {
        return ResourceType.UBO;
    }
}
