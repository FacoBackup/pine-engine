package com.pine.common.inspection;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

public class FieldDTO {
    private final FieldType type;
    private final String id;
    private final Field field;
    private final Object instance;
    private final List<SelectableEnum> options;
    private final InspectableField delegate;
    private final ListInspection listInspection;

    public FieldDTO(FieldType type, InspectableField delegate, Field field, Object instance, List<SelectableEnum> options, @Nullable ListInspection listInspection) {
        this.type = type;
        this.delegate = delegate;
        this.id = "##" + UUID.randomUUID().toString().replaceAll("-", "");
        this.field = field;
        this.instance = instance;
        this.options = options;
        this.listInspection = listInspection;
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

    public boolean isDisabled() {
        return delegate.disabled();
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

    public Class<?> getClassType() {
        if (isList()) {
            return listInspection.clazzType();
        }
        return null;
    }

    public boolean isList() {
        return listInspection != null;
    }
}
