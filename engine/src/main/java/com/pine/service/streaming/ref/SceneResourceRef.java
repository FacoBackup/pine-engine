package com.pine.service.streaming.ref;

import com.pine.repository.streaming.AbstractResourceRef;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.streaming.scene.SceneStreamData;

public class SceneResourceRef extends AbstractResourceRef<SceneStreamData> {
    public SceneResourceRef(String id) {
        super(id);
    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.SCENE;
    }

    @Override
    protected void loadInternal(SceneStreamData data) {
    }

    @Override
    protected void disposeInternal() {
    }
}
