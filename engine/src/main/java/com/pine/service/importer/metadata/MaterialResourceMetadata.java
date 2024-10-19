package com.pine.service.importer.metadata;

import com.pine.repository.streaming.StreamableResourceType;

public class MaterialResourceMetadata extends AbstractResourceMetadata {
    public MaterialResourceMetadata(String name, String id) {
        super(name, id);
    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.MATERIAL;
    }
}
