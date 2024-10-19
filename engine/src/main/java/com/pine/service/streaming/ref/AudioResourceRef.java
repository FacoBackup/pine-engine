package com.pine.service.streaming.ref;

import com.pine.repository.streaming.AbstractResourceRef;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.streaming.audio.AudioStreamData;

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
