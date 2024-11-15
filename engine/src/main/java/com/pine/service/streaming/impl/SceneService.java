package com.pine.service.streaming.impl;

import com.pine.FSUtil;
import com.pine.injection.PBean;
import com.pine.repository.streaming.AbstractResourceRef;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.importer.data.SceneImportData;
import com.pine.service.streaming.data.StreamData;
import com.pine.service.streaming.ref.SceneResourceRef;

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
