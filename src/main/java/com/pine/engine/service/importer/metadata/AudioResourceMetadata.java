package com.pine.engine.service.importer.metadata;

import com.pine.engine.repository.streaming.StreamableResourceType;

public class AudioResourceMetadata extends AbstractResourceMetadata {
    public AudioResourceMetadata(String name, String id) {
        super(name, id);
    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.AUDIO;
    }
}
