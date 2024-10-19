package com.pine.service.streaming.audio;

import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.streaming.StreamData;

public class AudioStreamData implements StreamData {
    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.AUDIO;
    }
}
