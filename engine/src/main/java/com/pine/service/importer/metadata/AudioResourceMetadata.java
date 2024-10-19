package com.pine.service.importer.metadata;

import com.pine.repository.streaming.StreamableResourceType;

public class AudioResourceMetadata extends AbstractResourceMetadata {
    public AudioResourceMetadata(String name, String id) {
        super(name, id);
    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.AUDIO;
    }
}
