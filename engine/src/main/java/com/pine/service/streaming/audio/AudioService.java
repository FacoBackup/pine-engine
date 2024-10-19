package com.pine.service.streaming.audio;

import com.pine.injection.PBean;
import com.pine.repository.streaming.AbstractResourceRef;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.streaming.AbstractStreamableService;
import com.pine.service.streaming.StreamData;
import com.pine.service.streaming.ref.AudioResourceRef;

@PBean
public class AudioService extends AbstractStreamableService<AudioResourceRef> {
    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.AUDIO;
    }

    @Override
    public StreamData stream(String pathToFile) {
        return null;
    }

    @Override
    public AbstractResourceRef<?> newInstance(String key) {
        return new AudioResourceRef(key);
    }
}
