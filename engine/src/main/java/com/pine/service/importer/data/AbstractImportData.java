package com.pine.service.importer.data;

import com.pine.inspection.Inspectable;
import com.pine.repository.streaming.StreamableResourceType;

import java.io.Serializable;
import java.util.UUID;

public abstract class AbstractImportData extends Inspectable implements Serializable {
    public final String id = UUID.randomUUID().toString();
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
