package com.pine.service.streaming.audio;

import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.streaming.StreamLoadData;

public class AudioStreamData implements StreamLoadData {
    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.AUDIO;
    }
}
