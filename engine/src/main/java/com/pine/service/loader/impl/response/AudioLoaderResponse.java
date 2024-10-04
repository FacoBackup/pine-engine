package com.pine.service.loader.impl.response;

import com.pine.repository.streaming.AudioStreamableResource;
import com.pine.repository.streaming.StreamableResourceType;

import java.util.List;

public class AudioLoaderResponse extends AbstractLoaderResponse<AudioStreamableResource> {

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.AUDIO;
    }

    public AudioLoaderResponse(boolean isLoaded, List<AudioStreamableResource> loadedResources) {
        super(isLoaded, loadedResources);
    }
}
