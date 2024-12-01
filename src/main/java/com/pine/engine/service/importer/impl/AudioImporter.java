package com.pine.engine.service.importer.impl;

import com.pine.common.injection.PBean;
import com.pine.engine.repository.streaming.StreamableResourceType;
import com.pine.engine.service.importer.AbstractImporter;
import com.pine.engine.service.importer.data.AbstractImportData;
import com.pine.engine.service.importer.data.AudioImportData;
import com.pine.engine.service.importer.metadata.AbstractResourceMetadata;
import com.pine.engine.service.importer.metadata.AudioResourceMetadata;

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
