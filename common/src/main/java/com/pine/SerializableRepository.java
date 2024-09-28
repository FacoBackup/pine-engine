package com.pine;

import imgui.ImVec4;
import imgui.type.ImInt;
import org.joml.Vector3f;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface SerializableRepository extends Serializable, Loggable {
    default void merge(Object data) {
        if (data.getClass() != this.getClass()) {
            getLogger().error("Classes are not compatible {} {}", data.getClass(), this.getClass());
            return;
        }
        Field[] declaredFields = this.getClass().getDeclaredFields();
        final Map<String, Field> fieldMap = new HashMap<>();

        for (Field sourceField : data.getClass().getDeclaredFields()) {
            fieldMap.put(sourceField.getName(), sourceField);
        }

        for (Field declaredField : declaredFields) {
            int modifiers = declaredField.getModifiers();
            if (Modifier.isTransient(modifiers) || Modifier.isPrivate(modifiers) || Modifier.isStatic(modifiers)) {
                continue;
            }

            try {
                var value = fieldMap.get(declaredField.getName()).get(data);
                if (Modifier.isFinal(modifiers)) {
                    var targetValue = fieldMap.get(declaredField.getName()).get(this);
                    if (value instanceof Vector3f) {
                        ((Vector3f) targetValue).set((Vector3f) value);
                    } else if (value instanceof Map) {
                        ((Map<?, ?>) targetValue).clear();
                        ((Map<?, ?>) targetValue).putAll((Map) value);
                    } else if (value instanceof ImVec4) {
                        ((ImVec4) targetValue).set((ImVec4) value);
                    } else if (value instanceof ImInt) {
                        ((ImInt) targetValue).set((ImInt) value);
                    } else if (value instanceof List) {
                        ((List<?>) targetValue).clear();
                        ((List<?>) targetValue).addAll((List) value);
                    }
                } else {
                    declaredField.setAccessible(true);
                    declaredField.set(this, value);
                }
            } catch (Exception e) {
                getLogger().error("Failed to update field {} onto {}", declaredField.getName(), this.getClass().getSimpleName(), e);
            }
        }
    }
}
