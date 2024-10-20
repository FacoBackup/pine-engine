package com.pine.service.importer.data;

import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.streaming.StreamData;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

public class SceneImportData extends AbstractImportData implements StreamData {
    @Serial
    private static final long serialVersionUID = 5832111484820460813L;

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
