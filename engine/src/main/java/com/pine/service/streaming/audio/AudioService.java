package com.pine.service.streaming.audio;

import com.pine.injection.PBean;
import com.pine.repository.streaming.AbstractResourceRef;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.streaming.AbstractStreamableService;
import com.pine.service.streaming.StreamData;
import com.pine.service.streaming.ref.AudioResourceRef;
import com.pine.service.streaming.ref.TextureResourceRef;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@PBean
public class AudioService extends AbstractStreamableService<AudioResourceRef> {
    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.AUDIO;
    }

    @Override
    public StreamData stream(String pathToFile, Map<String, StreamableResourceType> toBeStreamedIn) {
        return null;
    }

    @Override
    public AbstractResourceRef<?> newInstance(String key) {
        return new AudioResourceRef(key);
    }
}
