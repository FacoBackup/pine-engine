package com.pine.engine.service.streaming.ref;

import com.pine.engine.repository.streaming.AbstractResourceRef;
import com.pine.engine.repository.streaming.StreamableResourceType;
import com.pine.engine.service.streaming.data.AudioStreamData;

public class AudioResourceRef extends AbstractResourceRef<AudioStreamData> {

    public AudioResourceRef(String id) {
        super(id);
    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.AUDIO;
    }

    @Override
    protected void loadInternal(AudioStreamData data) {

    }

    @Override
    protected void disposeInternal() {

    }
}
