package com.pine.inspection;

import com.pine.component.EnumSelection;
import com.pine.component.SelectableEnum;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class WithMutableData {
    private transient final List<FieldDTO> fieldsAnnotated = new ArrayList<>();

    final public List<FieldDTO> getFieldsAnnotated() {
        if (fieldsAnnotated.isEmpty()) {
            for (var field : getClass().getFields()) {
                if (isMutableField(field)) {
                    FieldType fieldType = FieldType.getFieldType(field.getType());
                    if (fieldType != null) {
                        NumericFieldRule rules = field.getAnnotation(NumericFieldRule.class);
                        fieldsAnnotated.add(new FieldDTO(
                                fieldType,
                                field.getAnnotation(MutableField.class).label(),
                                field,
                                this,
                                rules != null ? rules.min() : null,
                                rules != null ? rules.max() : null,
                                rules != null && rules.isAngle(),
                                rules != null && rules.isDirectChange(),
                                getOptions(field)
                        ));
                    }
                }
            }
        }
        return fieldsAnnotated;
    }

    public abstract String getLabel();

    private List<SelectableEnum> getOptions(Field field) {
        EnumSelection selection = field.getAnnotation(EnumSelection.class);
        if (selection == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(Arrays.asList(selection.enumType().getEnumConstants()));
    }

    private static boolean isMutableField(Field field) {
        return !Modifier.isTransient(field.getModifiers()) && field.isAnnotationPresent(MutableField.class);
    }
}
