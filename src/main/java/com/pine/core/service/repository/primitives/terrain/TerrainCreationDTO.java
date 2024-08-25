package com.pine.core.service.repository.primitives.terrain;

import com.pine.core.service.ResourceType;
import com.pine.core.service.common.IResourceCreationData;

public class TerrainCreationDTO implements IResourceCreationData {
    @Override
    public ResourceType getResourceType() {
        return ResourceType.TERRAIN;
    }
}
