package com.pine.core.service.repository;

import com.pine.core.service.common.IResource;
import com.pine.core.service.common.IResourceRepository;
import com.pine.core.service.common.IResourceRuntimeData;
import com.pine.core.service.repository.primitives.EmptyRuntimeData;
import com.pine.core.service.repository.primitives.terrain.Terrain;
import com.pine.core.service.repository.primitives.terrain.TerrainCreationDTO;
import org.springframework.stereotype.Repository;

@Repository
public class TerrainRepository implements IResourceRepository<Terrain, EmptyRuntimeData, TerrainCreationDTO> {

    @Override
    public void bind(Terrain instance, EmptyRuntimeData data) {

    }

    @Override
    public void bind(Terrain instance) {

    }

    @Override
    public void unbind() {

    }

    @Override
    public IResource add(TerrainCreationDTO data) {
        return null;
    }

    @Override
    public void remove(Terrain id) {
        // TODO - Unload mesh and texture, since both will be linked directly to the terrain resource
    }
}
