package com.pine.engine.service.primitives.material;

import com.pine.engine.resource.IResourceCreationData;
import com.pine.engine.resource.IResourceRuntimeData;
import com.pine.engine.resource.ResourceType;

public class MaterialDTO implements IResourceRuntimeData, IResourceCreationData {
    @Override
    public ResourceType getResourceType() {
        return ResourceType.MATERIAL;
    }
}
