package com.pine.inspection;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

public class FieldDTO {
    private final FieldType type;
    private final String id;
    private final Field field;
    private final Object instance;
    private final List<SelectableEnum> options;
    private final MutableField delegate;

    public FieldDTO(FieldType type, MutableField delegate, Field field, Object instance, List<SelectableEnum> options) {
        this.type = type;
        this.delegate = delegate;
        this.id =  "##" + UUID.randomUUID().toString().replaceAll("-", "");
        this.field = field;
        this.instance = instance;
        this.options = options;
    }

    public String getId() {
        return id;
    }

    public FieldType getType() {
        return type;
    }

    public String getLabel() {
        return delegate.label();
    }

    public Field getField() {
        return field;
    }

    public Object getInstance() {
        return instance;
    }

    public int getMax() {
        return delegate.max();
    }

    public int getMin() {
        return delegate.min();
    }

    public boolean isAngle() {
        return delegate.isAngle();
    }

    public boolean isDirectChange() {
        return delegate.isDirectChange();
    }

    public List<SelectableEnum> getOptions() {
        return options;
    }

    public Object getValue() {
        try {
            return field.get(instance);
        } catch (Exception e) {
            return null;
        }
    }

    public String getGroup() {
        return delegate.group();
    }
}
