package com.pine.engine.service.streaming.impl;

import com.pine.FSUtil;
import com.pine.common.injection.PBean;
import com.pine.engine.repository.streaming.AbstractResourceRef;
import com.pine.engine.repository.streaming.StreamableResourceType;
import com.pine.engine.service.importer.data.SceneImportData;
import com.pine.engine.service.streaming.data.StreamData;
import com.pine.engine.service.streaming.ref.SceneResourceRef;

import java.util.Map;

@PBean
public class SceneService extends AbstractStreamableService<SceneResourceRef> {
    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.SCENE;
    }

    @Override
    public StreamData stream(String pathToFile, Map<String, StreamableResourceType> schedule, Map<String, AbstractResourceRef<?>> streamableResources) {
        return (SceneImportData) FSUtil.readBinary(pathToFile);
    }

    @Override
    public AbstractResourceRef<?> newInstance(String key) {
        return new SceneResourceRef(key);
    }
}
