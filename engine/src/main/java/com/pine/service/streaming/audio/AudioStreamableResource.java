package com.pine.service.streaming.audio;

import com.pine.repository.streaming.AbstractStreamableResource;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.theme.Icons;

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

    @Override
    public String getIcon() {
        return Icons.audiotrack;
    }
}
