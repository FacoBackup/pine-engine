package com.pine.service.streaming.ref;

import com.pine.repository.streaming.AbstractResourceRef;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.importer.data.SceneImportData;

public class SceneResourceRef extends AbstractResourceRef<SceneImportData> {
    public SceneResourceRef(String id) {
        super(id);
    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.SCENE;
    }

    @Override
    protected void loadInternal(SceneImportData data) {
    }

    @Override
    protected void disposeInternal() {
    }
}
