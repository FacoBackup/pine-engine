package com.pine.engine.service.importer.data;

import com.pine.engine.repository.streaming.StreamableResourceType;

public class AudioImportData extends AbstractImportData{
    public AudioImportData(String name) {
        super(name);
    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.AUDIO;
    }
}
