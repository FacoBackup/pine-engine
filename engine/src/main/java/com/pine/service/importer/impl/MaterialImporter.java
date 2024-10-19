package com.pine.service.importer.impl;

import com.google.gson.Gson;
import com.pine.injection.PBean;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.importer.AbstractImporter;
import com.pine.service.importer.data.AbstractImportData;
import com.pine.service.importer.data.MaterialImportData;
import com.pine.service.importer.data.MeshImportData;
import com.pine.service.importer.metadata.AbstractResourceMetadata;
import com.pine.service.importer.metadata.MaterialResourceMetadata;
import com.pine.service.importer.metadata.TextureResourceMetadata;

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
}
