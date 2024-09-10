package com.pine.engine.core.service.resource;

import com.pine.engine.core.service.resource.primitives.EmptyRuntimeData;
import com.pine.engine.core.service.resource.primitives.texture.Texture;
import com.pine.engine.core.service.resource.primitives.texture.TextureCreationData;
import com.pine.engine.core.service.resource.resource.AbstractResourceService;
import com.pine.engine.core.service.resource.resource.IResource;
import com.pine.engine.core.service.resource.resource.ResourceType;

public class TextureService extends AbstractResourceService<Texture, EmptyRuntimeData, TextureCreationData> {

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
    protected IResource addInternal(TextureCreationData data) {
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