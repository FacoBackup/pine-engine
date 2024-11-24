package com.pine.engine.service.streaming.data;

import com.pine.engine.repository.streaming.StreamableResourceType;

public class AudioStreamData implements StreamData {
    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.AUDIO;
    }
}
