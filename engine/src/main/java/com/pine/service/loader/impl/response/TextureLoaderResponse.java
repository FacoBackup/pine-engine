package com.pine.service.loader.impl.response;

import com.pine.repository.streaming.StreamableResourceType;
import com.pine.repository.streaming.TextureStreamableResource;

import java.util.List;

public class TextureLoaderResponse extends AbstractLoaderResponse<TextureStreamableResource> {

    public TextureLoaderResponse(boolean isLoaded, List<TextureStreamableResource> loadedResources) {
        super(isLoaded, loadedResources);
    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.TEXTURE;
    }
}

