package com.pine.core.service.repository;

import com.pine.core.service.common.IResource;
import com.pine.core.service.common.IResourceRepository;
import com.pine.core.service.common.IResourceRuntimeData;
import com.pine.core.service.repository.primitives.EmptyRuntimeData;
import com.pine.core.service.repository.primitives.terrain.Terrain;
import com.pine.core.service.repository.primitives.terrain.TerrainCreationDTO;
import org.springframework.stereotype.Repository;

@Repository
public class TerrainRepository implements IResourceRepository< EmptyRuntimeData, TerrainCreationDTO> {

    @Override
    public void bind(String id, EmptyRuntimeData data) {

    }

    @Override
    public void bind(String id) {

    }

    @Override
    public void unbind() {

    }

    @Override
    public <T extends IResource> T add(TerrainCreationDTO data) {
        return null;
    }

    @Override
    public void remove(String id) {

    }
}
