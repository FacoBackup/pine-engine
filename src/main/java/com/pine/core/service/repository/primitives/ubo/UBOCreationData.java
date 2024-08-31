package com.pine.core.service.repository.primitives.ubo;

import com.pine.core.service.ResourceType;
import com.pine.core.service.common.IResourceCreationData;

import java.util.List;

public record UBOCreationData(List<UBOData> data, String blockName) implements IResourceCreationData {
    @Override
    public ResourceType getResourceType() {
        return ResourceType.UBO;
    }
}
