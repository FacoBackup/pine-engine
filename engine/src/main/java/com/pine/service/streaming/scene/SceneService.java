package com.pine.service.streaming.scene;

import com.pine.FSUtil;
import com.pine.injection.PBean;
import com.pine.repository.streaming.AbstractResourceRef;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.streaming.AbstractStreamableService;
import com.pine.service.streaming.StreamData;
import com.pine.service.streaming.ref.SceneResourceRef;

import java.util.List;
import java.util.Map;

@PBean
public class SceneService extends AbstractStreamableService<SceneResourceRef> {
    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.SCENE;
    }

    @Override
    public StreamData stream(String pathToFile, Map<String, StreamableResourceType> toBeStreamedIn) {
        return (SceneStreamData) FSUtil.read(pathToFile);
    }

    @Override
    public AbstractResourceRef<?> newInstance(String key) {
        return new SceneResourceRef(key);
    }
}
