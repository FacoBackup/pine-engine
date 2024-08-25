package com.pine.core.service.repository;

import com.pine.core.service.common.IResource;
import com.pine.core.service.common.IResourceRepository;
import com.pine.core.service.repository.primitives.material.Material;
import com.pine.core.service.repository.primitives.material.MaterialDTO;
import org.springframework.stereotype.Repository;

@Repository
public class MaterialRepository implements IResourceRepository<MaterialDTO, MaterialDTO> {

    @Override
    public void bind(String id, MaterialDTO data) {

    }

    @Override
    public void bind(String id) {

    }

    @Override
    public void unbind() {

    }

    @Override
    public <T extends IResource> T add(MaterialDTO data) {
        return null;
    }

    @Override
    public void remove(String id) {

    }
}
