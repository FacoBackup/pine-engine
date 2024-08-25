package com.pine.core.service.repository.primitives.material;

import com.pine.core.service.ResourceType;
import com.pine.core.service.common.IResourceCreationData;
import com.pine.core.service.common.IResourceRuntimeData;

public class MaterialDTO implements IResourceRuntimeData, IResourceCreationData {
    @Override
    public ResourceType getResourceType() {
        return ResourceType.MATERIAL;
    }
}
