package com.pine.core.service.repository;

import com.pine.core.service.common.IResource;
import com.pine.core.service.common.IResourceRuntimeData;
import com.pine.core.service.repository.primitives.EmptyRuntimeData;
import com.pine.core.service.repository.primitives.mesh.Mesh;
import com.pine.core.service.common.IResourceRepository;
import com.pine.core.service.repository.primitives.mesh.MeshDTO;
import org.springframework.stereotype.Repository;

@Repository
public class MeshRepository implements IResourceRepository<Mesh, EmptyRuntimeData, MeshDTO> {

    @Override
    public void bind(Mesh instance, EmptyRuntimeData data) {

    }

    @Override
    public void bind(Mesh instance) {

    }

    @Override
    public void unbind() {

    }

    @Override
    public IResource add(MeshDTO data) {
        return null;
    }

    @Override
    public void remove(Mesh id) {
        // TODO - remove last used and unbind if is bound for some reason (probably will never happen)
    }
}
