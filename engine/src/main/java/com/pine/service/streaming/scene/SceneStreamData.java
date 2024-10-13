package com.pine.service.streaming.scene;

import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.streaming.StreamLoadData;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SceneStreamData implements StreamLoadData {
    public final List<SceneStreamData> children = new ArrayList<>();
    public final String id = UUID.randomUUID().toString();
    public final String name;
    public String meshResourceId;
    public String materialResourceId;

    public SceneStreamData(String name) {
        this.name = name;
    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.SCENE;
    }
}
