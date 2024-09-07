package com.pine.engine.core.service;

import com.pine.engine.core.resource.AbstractResourceService;
import com.pine.engine.core.resource.IResource;
import com.pine.engine.core.resource.ResourceType;
import com.pine.engine.core.service.primitives.material.Material;
import com.pine.engine.core.service.primitives.material.MaterialDTO;

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
