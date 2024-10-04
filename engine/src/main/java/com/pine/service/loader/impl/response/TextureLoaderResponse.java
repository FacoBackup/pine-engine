package com.pine.service.loader.impl.response;

import com.pine.repository.streaming.StreamableResourceType;
import com.pine.repository.streaming.TextureStreamableResource;
import com.pine.service.loader.impl.info.LoadRequest;

import java.util.List;

public class TextureLoaderResponse extends AbstractLoaderResponse<TextureStreamableResource> {

    public TextureLoaderResponse(boolean isLoaded, LoadRequest request, List<TextureStreamableResource> loadedResources) {
        super(isLoaded, request, loadedResources);
    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.TEXTURE;
    }
}

