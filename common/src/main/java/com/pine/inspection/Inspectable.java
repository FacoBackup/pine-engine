package com.pine.inspection;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class Inspectable {
    private transient List<FieldDTO> fieldsAnnotated = new ArrayList<>();
    private transient List<MethodDTO> methodsAnnotated = new ArrayList<>();
    private transient boolean isCollectedFields = false;
    private transient boolean isCollectedMethods = false;

    final public List<FieldDTO> getFieldsAnnotated() {
        if (fieldsAnnotated == null) {
            fieldsAnnotated = new ArrayList<>();
        }
        if (fieldsAnnotated.isEmpty() && !isCollectedFields) {
            for (var field : getClass().getFields()) {
                InspectableField inspectableField = field.getAnnotation(InspectableField.class);
                if (inspectableField != null) {
                    FieldType fieldType = FieldType.getFieldType(field.getType());
                    if (fieldType != null) {
                        fieldsAnnotated.add(new FieldDTO(
                                fieldType,
                                inspectableField,
                                field,
                                this,
                                getOptions(field)
                        ));
                    }
                }
            }
            isCollectedFields = true;
        }
        return fieldsAnnotated;
    }

    final public List<MethodDTO> getMethodsAnnotated() {
        if (methodsAnnotated == null) {
            methodsAnnotated = new ArrayList<>();
        }
        if (methodsAnnotated.isEmpty() && !isCollectedMethods) {
            for (var method : getClass().getMethods()) {
                ExecutableField methodField = method.getAnnotation(ExecutableField.class);
                if (methodField != null) {
                    methodsAnnotated.add(new MethodDTO(
                            methodField,
                            method,
                            this
                    ));
                }
            }
            isCollectedMethods = true;
        }
        return methodsAnnotated;
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
