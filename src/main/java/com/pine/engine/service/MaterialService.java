package com.pine.engine.service;

import com.pine.common.resource.IResource;
import com.pine.common.resource.ResourceService;
import com.pine.engine.service.primitives.material.Material;
import com.pine.engine.service.primitives.material.MaterialDTO;
import org.springframework.stereotype.Repository;

@Repository
public class MaterialService implements ResourceService<Material, MaterialDTO, MaterialDTO> {

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
