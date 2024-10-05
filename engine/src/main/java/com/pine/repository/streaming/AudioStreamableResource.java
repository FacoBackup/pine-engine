package com.pine.repository.streaming;

import com.pine.service.streaming.audio.AudioStreamData;

public class AudioStreamableResource extends AbstractStreamableResource<AudioStreamData> {

    public AudioStreamableResource(String pathToFile, String id) {
        super(pathToFile, id);
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
