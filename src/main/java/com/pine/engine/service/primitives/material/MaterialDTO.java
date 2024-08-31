package com.pine.engine.service.primitives.material;

import com.pine.common.resource.ResourceType;
import com.pine.common.resource.IResourceCreationData;
import com.pine.common.resource.IResourceRuntimeData;

public class MaterialDTO implements IResourceRuntimeData, IResourceCreationData {
    @Override
    public ResourceType getResourceType() {
        return ResourceType.MATERIAL;
    }
}
