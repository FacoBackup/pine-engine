package com.pine.service.importer.impl;

import com.pine.injection.PBean;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.importer.AbstractImporter;
import com.pine.service.importer.data.AbstractImportData;
import com.pine.service.importer.data.SceneImportData;
import com.pine.service.importer.metadata.AbstractResourceMetadata;
import com.pine.service.importer.metadata.SceneResourceMetadata;

@PBean
public class SceneImporter extends AbstractImporter {
    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.SCENE;
    }

    private int total = 1;
    @Override
    public AbstractResourceMetadata persist(AbstractImportData data) {
        persistInternal(data);
        total = 1;
        traverse((SceneImportData) data);
        return new SceneResourceMetadata(data.name, data.id, total);
    }

    private void traverse(SceneImportData cast) {
        for(var child : cast.children){
            total++;
            traverse(child);
        }
    }
}
