package com.pine.engine.service.importer.data;

import com.pine.common.inspection.Inspectable;
import com.pine.engine.repository.streaming.StreamableResourceType;

import java.io.Serializable;
import java.util.UUID;

public abstract class AbstractImportData extends Inspectable implements Serializable {
    public String id = UUID.randomUUID().toString();
    public String name;

    public AbstractImportData(String name) {
        this.name = name;
    }

    public abstract StreamableResourceType getResourceType();

    @Override
    public String getTitle() {
        return getResourceType().getTitle() + " - " + id;
    }

    @Override
    public String getIcon() {
        return getResourceType().getIcon();
    }
}
