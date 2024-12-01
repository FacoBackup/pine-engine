package com.pine.engine.service.importer.metadata;

import com.pine.common.inspection.InspectableField;
import com.pine.engine.repository.streaming.StreamableResourceType;

public class SceneResourceMetadata extends AbstractResourceMetadata {

    @InspectableField(label = "Number of children", disabled = true)
    public final int totalNumberOfChildren;

    public SceneResourceMetadata(String name, String id, int total) {
        super(name, id);
        this.totalNumberOfChildren = total;
    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.SCENE;
    }
}
