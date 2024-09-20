package com.pine.inspection;

import com.pine.service.resource.resource.ResourceType;

import java.lang.reflect.Field;
import java.util.UUID;

public class FieldDTO {
    private final ResourceType resourceType;
    private final FieldType type;
    private final String label;
    private final String labelWithId;
    private final Field field;
    private final Object instance;
    private final int max;
    private final int min;
    private final boolean angle;
    private final boolean directChange;

    public FieldDTO(FieldType type, String label, Field field, Object instance, Integer max, Integer min, boolean isAngle, boolean isDirectChange, ResourceType resourceType) {
        this.type = type;
        this.resourceType = resourceType;
        this.label = label ;
        this.labelWithId = label + "#" + UUID.randomUUID().toString().replaceAll("-", "");
        this.field = field;
        this.instance = instance;
        this.max = max == null ? Integer.MAX_VALUE : max;
        this.min = min == null ? Integer.MIN_VALUE : min;
        this.angle = isAngle;
        this.directChange = isDirectChange;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public String getLabelWithId() {
        return labelWithId;
    }

    public FieldType getType() {
        return type;
    }

    public String getLabel() {
        return label;
    }

    public Field getField() {
        return field;
    }

    public Object getInstance() {
        return instance;
    }

    public int getMax() {
        return max;
    }

    public int getMin() {
        return min;
    }

    public boolean isAngle() {
        return angle;
    }

    public boolean isDirectChange() {
        return directChange;
    }
}
