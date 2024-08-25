package com.pine.core.service.repository;

import com.pine.core.service.common.IResource;
import com.pine.core.service.common.IResourceRuntimeData;
import com.pine.core.service.repository.primitives.EmptyRuntimeData;
import com.pine.core.service.repository.primitives.mesh.Mesh;
import com.pine.core.service.common.IResourceRepository;
import com.pine.core.service.repository.primitives.mesh.MeshDTO;
import org.springframework.stereotype.Repository;

@Repository
public class MeshRepository implements IResourceRepository<EmptyRuntimeData, MeshDTO> {
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
    public <T extends IResource> T add(MeshDTO data) {
        return null;
    }

    @Override
    public void remove(String id) {

    }
}
