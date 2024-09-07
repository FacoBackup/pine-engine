package com.pine.engine.service;

import com.pine.engine.resource.AbstractResourceService;
import com.pine.engine.resource.IResource;
import com.pine.engine.resource.ResourceType;
import com.pine.engine.service.primitives.material.Material;
import com.pine.engine.service.primitives.material.MaterialDTO;

public class MaterialService extends AbstractResourceService<Material, MaterialDTO, MaterialDTO> {

    @Override
    protected void bindInternal(Material instance, MaterialDTO data) {

    }

    @Override
    protected void bindInternal(Material instance) {

    }

    @Override
    public void unbind() {

    }

    @Override
    protected IResource addInternal(MaterialDTO data) {
        return null;
    }

    @Override
    protected void removeInternal(Material id) {
        // TODO - Remove shader, textures will be GCed dynamically
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.MATERIAL;
    }
}
