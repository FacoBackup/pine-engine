package com.pine.inspection;

import com.pine.service.resource.resource.IResource;
import org.joml.*;

public enum FieldType {
    RESOURCE(IResource.class),
    STRING(String.class),
    INT(int.class),
    LONG(long.class),
    FLOAT(float.class),
    DOUBLE(double.class),
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
            if (ft.getClazz() == clazz) {
                return ft;
            }
        }
        return null;
    }
}
