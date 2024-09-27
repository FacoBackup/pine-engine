package com.pine.inspection;

import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;

public enum FieldType {
    LIST(List.class),
    COMPOSITE(Inspectable.class),
    CUSTOM(null),
    COLOR(Color.class),
    OPTIONS(SelectableEnum.class),
    STRING(String.class),
    INT(int.class),
    FLOAT(float.class),
    BOOLEAN(boolean.class),
    VECTOR2(Vector2f.class),
    VECTOR3(Vector3f.class),
    VECTOR4(Vector4f.class),
    QUATERNION(Quaternionf.class);

    private final Class<?> clazz;

    FieldType(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public static FieldType getFieldType(Class<?> clazz) {
        for (FieldType ft : FieldType.values()) {
            // SPECIFIC
            if (ft != CUSTOM && ft.getClazz() == clazz) {
                return ft;
            }
        }

        for (FieldType ft : FieldType.values()) {
            // INSTANCE OF
            if (ft != CUSTOM && ft.getClazz().isInstance(clazz)) {
                return ft;
            }
        }
        return CUSTOM;
    }
}
