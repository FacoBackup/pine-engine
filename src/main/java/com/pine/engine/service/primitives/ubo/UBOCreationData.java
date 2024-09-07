package com.pine.engine.service.primitives.ubo;

import com.pine.engine.resource.IResourceCreationData;
import com.pine.engine.resource.ResourceType;

import java.util.List;

public record UBOCreationData(List<UBOData> data, String blockName) implements IResourceCreationData {
    @Override
    public ResourceType getResourceType() {
        return ResourceType.UBO;
    }
}
