package com.pine.engine.core.service.primitives.material;

import com.pine.engine.core.resource.IResourceCreationData;
import com.pine.engine.core.resource.IResourceRuntimeData;
import com.pine.engine.core.resource.ResourceType;

public class MaterialDTO implements IResourceRuntimeData, IResourceCreationData {
    @Override
    public ResourceType getResourceType() {
        return ResourceType.MATERIAL;
    }
}
