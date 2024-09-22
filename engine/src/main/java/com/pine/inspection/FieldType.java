package com.pine.inspection;

import com.pine.component.SelectableEnum;
import com.pine.component.rendering.CompositeScene;
import com.pine.service.resource.resource.IResource;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public enum FieldType {
    SCENE(CompositeScene.class),
    RESOURCE(ResourceRef.class),
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
            if (ft.getClazz() == clazz) {
                return ft;
            }
        }

        for (FieldType ft : FieldType.values()) {
            // INSTANCE OF
            if (ft.getClazz().isInstance(clazz)) {
                return ft;
            }
        }
        return null;
    }
}
