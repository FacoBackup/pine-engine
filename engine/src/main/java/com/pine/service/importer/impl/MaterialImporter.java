package com.pine.service.importer.impl;

import com.pine.injection.PBean;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.importer.AbstractImporter;
import com.pine.service.importer.data.AbstractImportData;
import com.pine.service.importer.data.MaterialImportData;
import com.pine.service.importer.metadata.AbstractResourceMetadata;
import com.pine.service.importer.metadata.MaterialResourceMetadata;

@PBean
public class MaterialImporter extends AbstractImporter {
    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.MATERIAL;
    }

    @Override
    public AbstractResourceMetadata persist(AbstractImportData data) {
        persistInternal(data);
        return new MaterialResourceMetadata(data.name, data.id);
    }

    @Override
    public AbstractResourceMetadata createNew() {
        return persist(new MaterialImportData("New material"));
    }
}
