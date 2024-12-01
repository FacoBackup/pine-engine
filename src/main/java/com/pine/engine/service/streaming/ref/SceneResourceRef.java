package com.pine.engine.service.streaming.ref;

import com.pine.engine.repository.streaming.AbstractResourceRef;
import com.pine.engine.repository.streaming.StreamableResourceType;
import com.pine.engine.service.importer.data.SceneImportData;

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
