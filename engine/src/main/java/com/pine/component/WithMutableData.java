package com.pine.component;

import com.pine.inspection.FieldDTO;
import com.pine.inspection.FieldType;
import com.pine.inspection.MutableField;
import com.pine.inspection.NumericFieldRule;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class WithMutableData {
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
                                rules != null && rules.isDirectChange()
                        ));
                    }
                }
            }
        }
        return fieldsAnnotated;
    }

    private static boolean isMutableField(Field field) {
        return !Modifier.isTransient(field.getModifiers()) && !Modifier.isFinal(field.getModifiers()) && field.isAnnotationPresent(MutableField.class);
    }
}
