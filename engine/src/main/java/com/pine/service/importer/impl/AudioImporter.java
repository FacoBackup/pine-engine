package com.pine.service.importer.impl;

import com.pine.injection.PBean;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.importer.AbstractImporter;
import com.pine.service.importer.data.AbstractImportData;
import com.pine.service.importer.data.AudioImportData;
import com.pine.service.importer.data.MaterialImportData;
import com.pine.service.importer.metadata.AbstractResourceMetadata;
import com.pine.service.importer.metadata.AudioResourceMetadata;
import com.pine.service.importer.metadata.TextureResourceMetadata;

import java.util.Collections;
import java.util.List;

@PBean
public class AudioImporter extends AbstractImporter {

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.AUDIO;
    }

    @Override
    public AbstractResourceMetadata persist(AbstractImportData data) {
        var cast = (AudioImportData) data;
        var file = persistInternal(data);
        return new AudioResourceMetadata(data.name, data.id);
    }
}
