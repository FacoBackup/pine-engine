package com.pine.service.streaming.audio;

import com.pine.injection.PBean;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.streaming.AbstractStreamableService;

@PBean
public class AudioService extends AbstractStreamableService<AudioStreamableResource, AudioStreamData> {
    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.AUDIO;
    }

    @Override
    public AudioStreamData stream(String path) {
        return null;
    }
}
