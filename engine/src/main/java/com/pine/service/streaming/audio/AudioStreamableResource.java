package com.pine.service.streaming.audio;

import com.pine.repository.streaming.AbstractStreamableResource;
import com.pine.repository.streaming.StreamableResourceType;

public class AudioStreamableResource extends AbstractStreamableResource<AudioStreamData> {
    @Override
    protected void disposeInternal() {

    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.AUDIO;
    }

    @Override
    protected void loadInternal(AudioStreamData data) {

    }
}
