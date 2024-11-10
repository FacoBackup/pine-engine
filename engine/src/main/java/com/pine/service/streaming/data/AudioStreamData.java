package com.pine.service.streaming.data;

import com.pine.repository.streaming.StreamableResourceType;

public class AudioStreamData implements StreamData {
    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.AUDIO;
    }
}
