package com.pine.engine.service.importer.impl;

import com.pine.common.injection.PBean;
import com.pine.engine.repository.streaming.StreamableResourceType;
import com.pine.engine.service.importer.metadata.AbstractResourceMetadata;
import com.pine.engine.service.importer.metadata.MaterialResourceMetadata;
import com.pine.engine.service.importer.AbstractImporter;
import com.pine.engine.service.importer.data.AbstractImportData;
import com.pine.engine.service.importer.data.MaterialImportData;

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
