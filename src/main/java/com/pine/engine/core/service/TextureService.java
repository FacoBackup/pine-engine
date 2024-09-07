package com.pine.engine.core.service;

import com.pine.engine.core.resource.AbstractResourceService;
import com.pine.engine.core.resource.IResource;
import com.pine.engine.core.resource.ResourceType;
import com.pine.engine.core.service.primitives.EmptyRuntimeData;
import com.pine.engine.core.service.primitives.texture.Texture;
import com.pine.engine.core.service.primitives.texture.TextureCreationDTO;

public class TextureService extends AbstractResourceService<Texture, EmptyRuntimeData, TextureCreationDTO> {

    @Override
    protected void bindInternal(Texture instance, EmptyRuntimeData data) {

    }

    @Override
    protected void bindInternal(Texture instance) {

    }

    @Override
    public void unbind() {

    }

    @Override
    protected IResource addInternal(TextureCreationDTO data) {
        return null;
    }

    @Override
    public void removeInternal(Texture id) {

    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.TEXTURE;
    }
}
