package com.pine.service.streaming.texture;

import com.pine.injection.PBean;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.repository.streaming.TextureStreamableResource;
import com.pine.service.streaming.AbstractStreamableService;

@PBean
public class TextureService extends AbstractStreamableService<TextureStreamableResource> {

    @Override
    protected void bindInternal() {

    }

    @Override
    public void unbind() {

    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.TEXTURE;
    }
}
