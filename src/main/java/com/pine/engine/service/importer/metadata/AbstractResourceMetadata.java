package com.pine.engine.service.importer.metadata;

import com.pine.common.inspection.Inspectable;
import com.pine.common.inspection.InspectableField;
import com.pine.engine.repository.streaming.StreamableResourceType;
import com.pine.engine.service.importer.ImporterService;

import java.io.Serializable;

public abstract class AbstractResourceMetadata extends Inspectable implements Serializable {
    public final String id;
    public final String name;

    @InspectableField(label = "Size", disabled = true)
    public String sizeWithUnit;
    private float size;

    public AbstractResourceMetadata(String name, String id) {
        this.id = id;
        this.name = name;
    }

    public abstract StreamableResourceType getResourceType();

    @Override
    public String getTitle() {
        return getResourceType().getTitle();
    }

    @Override
    public String getIcon() {
        return getResourceType().getIcon();
    }

    public void setSize(float size) {
        this.sizeWithUnit = ImporterService.getSizeWithUnit(size);
        this.size = size;
    }

    public String getSizeWithUnit() {
        return sizeWithUnit;
    }

    public float getSize() {
        return size;
    }
}
