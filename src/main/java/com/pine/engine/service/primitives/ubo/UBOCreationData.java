package com.pine.engine.service.primitives.ubo;

import com.pine.common.resource.ResourceType;
import com.pine.common.resource.IResourceCreationData;

import java.util.List;

public record UBOCreationData(List<UBOData> data, String blockName) implements IResourceCreationData {
    @Override
    public ResourceType getResourceType() {
        return ResourceType.UBO;
    }
}
