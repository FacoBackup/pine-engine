package com.pine.service.importer.data;

import com.pine.repository.streaming.StreamableResourceType;

public class AudioImportData extends AbstractImportData{
    public AudioImportData(String name) {
        super(name);
    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.AUDIO;
    }
}
