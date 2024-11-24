package com.pine.engine.service.importer.impl;

import com.pine.common.injection.PBean;
import com.pine.engine.repository.streaming.StreamableResourceType;
import com.pine.engine.service.importer.metadata.AbstractResourceMetadata;
import com.pine.engine.service.importer.metadata.SceneResourceMetadata;
import com.pine.engine.service.importer.AbstractImporter;
import com.pine.engine.service.importer.data.AbstractImportData;
import com.pine.engine.service.importer.data.SceneImportData;

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
