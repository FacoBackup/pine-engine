package com.pine.service.resource;

import com.pine.PBean;
import com.pine.service.resource.primitives.EmptyRuntimeData;
import com.pine.service.resource.primitives.texture.TextureResource;
import com.pine.service.resource.primitives.texture.TextureCreationData;
import com.pine.service.resource.resource.AbstractResourceService;
import com.pine.service.resource.resource.IResource;
import com.pine.service.resource.resource.ResourceType;

@PBean
public class TextureService extends AbstractResourceService<TextureResource, EmptyRuntimeData, TextureCreationData> {

    @Override
    protected void bindInternal(TextureResource instance, EmptyRuntimeData data) {

    }

    @Override
    protected void bindInternal(TextureResource instance) {

    }

    @Override
    public void unbind() {

    }

    @Override
    protected IResource addInternal(TextureCreationData data) {
        return null;
    }

    @Override
    public void removeInternal(TextureResource id) {

    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.TEXTURE;
    }
}
