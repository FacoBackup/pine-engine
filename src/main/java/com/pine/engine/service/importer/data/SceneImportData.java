package com.pine.engine.service.importer.data;

import com.pine.engine.repository.streaming.StreamableResourceType;
import com.pine.engine.service.streaming.data.StreamData;

import java.util.ArrayList;
import java.util.List;

public class SceneImportData extends AbstractImportData implements StreamData {
    public final List<SceneImportData> children = new ArrayList<>();
    public String meshResourceId;
    public String materialResourceId;

    public SceneImportData(String name) {
        super(name);
    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.SCENE;
    }
}
