package com.pine.service.importer.metadata;

import com.pine.repository.streaming.StreamableResourceType;

import java.io.Serializable;

public abstract class AbstractResourceMetadata implements Serializable {
    public final String id;
    public String name;

    public AbstractResourceMetadata(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public abstract StreamableResourceType getResourceType();
}
