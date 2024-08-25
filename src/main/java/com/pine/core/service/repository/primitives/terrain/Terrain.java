package com.pine.core.service.repository.primitives.terrain;

import com.pine.core.service.ResourceType;
import com.pine.core.service.common.AbstractResource;

public class Terrain extends AbstractResource<TerrainCreationDTO> {
    public Terrain(String id, TerrainCreationDTO dto) {
        super(id);
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.TERRAIN;
    }
}
