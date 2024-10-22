package com.pine.service.importer.metadata;

import com.pine.inspection.Inspectable;
import com.pine.inspection.InspectableField;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.importer.ImporterService;

import java.io.Serializable;

public abstract class AbstractResourceMetadata extends Inspectable implements Serializable {

    @InspectableField(label = "Name")
    public String name;

    @InspectableField(label = "Identifier", disabled = true)
    public final String id;

    @InspectableField(label = "Size", disabled = true)
    public String sizeWithUnit;

    private float size;

    public AbstractResourceMetadata(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public abstract StreamableResourceType getResourceType();

    @Override
    public String getTitle() {
        return name;
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
