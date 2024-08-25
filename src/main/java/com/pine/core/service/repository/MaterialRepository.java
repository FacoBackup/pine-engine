package com.pine.core.service.repository;

import com.pine.core.service.common.IResource;
import com.pine.core.service.common.IResourceRepository;
import com.pine.core.service.repository.primitives.material.Material;
import com.pine.core.service.repository.primitives.material.MaterialDTO;
import org.springframework.stereotype.Repository;

@Repository
public class MaterialRepository implements IResourceRepository<Material, MaterialDTO, MaterialDTO> {

    @Override
    public void bind(Material instance, MaterialDTO data) {

    }

    @Override
    public void bind(Material instance) {

    }

    @Override
    public void unbind() {

    }

    @Override
    public IResource add(MaterialDTO data) {
        return null;
    }

    @Override
    public void remove(Material id) {
        // TODO - Remove shader, textures will be GCed dynamically
    }
}
