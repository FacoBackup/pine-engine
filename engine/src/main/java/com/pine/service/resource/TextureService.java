package com.pine.service.resource;

import com.pine.PBean;
import com.pine.service.resource.primitives.EmptyRuntimeData;
import com.pine.service.resource.primitives.texture.TextureCreationData;
import com.pine.service.resource.primitives.texture.Texture;
import com.pine.service.resource.resource.AbstractResourceService;
import com.pine.service.resource.resource.IResource;
import com.pine.service.resource.resource.ResourceType;

@PBean
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
