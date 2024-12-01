package com.pine.engine.service.importer.metadata;

import com.pine.engine.repository.streaming.StreamableResourceType;

public class MaterialResourceMetadata extends AbstractResourceMetadata {
    public MaterialResourceMetadata(String name, String id) {
        super(name, id);
    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.MATERIAL;
    }
}
