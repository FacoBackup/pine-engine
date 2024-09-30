package com.pine.inspection;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class Inspectable {
    private transient final List<FieldDTO> fieldsAnnotated = new ArrayList<>();

    final public List<FieldDTO> getFieldsAnnotated() {
        if (fieldsAnnotated.isEmpty()) {
            for (var field : getClass().getFields()) {
                MutableField mutableField = field.getAnnotation(MutableField.class);
                if (mutableField != null) {
                    FieldType fieldType = FieldType.getFieldType(field.getType());
                    if (fieldType != null) {
                        fieldsAnnotated.add(new FieldDTO(
                                fieldType,
                                mutableField,
                                field,
                                this,
                                getOptions(field)
                        ));
                    }
                }
            }
        }
        return fieldsAnnotated;
    }

    public abstract String getTitle();

    public abstract String getIcon();

    @SuppressWarnings("unchecked")
    private List<SelectableEnum> getOptions(Field field) {
        if (!field.getType().isAssignableFrom(SelectableEnum.class)) {
            return Collections.emptyList();
        }
        return new ArrayList<>(Arrays.asList(((Class<SelectableEnum>) field.getType()).getEnumConstants()));
    }
}
