package com.pine.service.streaming.impl;

import com.pine.injection.PBean;
import com.pine.repository.streaming.AbstractResourceRef;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.streaming.data.StreamData;
import com.pine.service.streaming.ref.AudioResourceRef;

import java.util.Map;

@PBean
public class AudioService extends AbstractStreamableService<AudioResourceRef> {
    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.AUDIO;
    }

    @Override
    public StreamData stream(String pathToFile, Map<String, StreamableResourceType> schedule, Map<String, AbstractResourceRef<?>> streamableResources) {
        return null;
    }

    @Override
    public AbstractResourceRef<?> newInstance(String key) {
        return new AudioResourceRef(key);
    }
}
