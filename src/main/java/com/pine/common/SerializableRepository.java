package com.pine.common;

import com.pine.common.injection.PInject;
import com.pine.common.messaging.Loggable;
import imgui.ImVec4;
import imgui.type.ImInt;
import org.joml.Vector3f;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

public interface SerializableRepository extends Serializable, Loggable {
    default void merge(Object data) {
        if (data == null) {
            return;
        }
        if (SerializationState.loaded.containsKey(this)) {
            return;
        }

        SerializationState.loaded.put(this, true);
        for (Field declaredField : this.getClass().getFields()) {
            int modifiers = declaredField.getModifiers();
            if (Modifier.isTransient(modifiers) || Modifier.isPrivate(modifiers) || Modifier.isStatic(modifiers)) {
                continue;
            }

            try {
                declaredField.setAccessible(true);
                var value = data.getClass().getField(declaredField.getName()).get(data);
                var targetValue = declaredField.get(this);

                if (declaredField.isAnnotationPresent(PInject.class)) {
                    if (List.class.isAssignableFrom(declaredField.getType())) {
                        List<?> t = (List<?>) targetValue;
                        List<?> tO = (List<?>) value;
                        t.forEach(a -> {
                            if (a instanceof SerializableRepository) {
                                ((SerializableRepository) a).merge(tO.stream().filter(b -> b.getClass() == a.getClass()).findFirst().orElse(null));
                            }
                        });
                    } else if (targetValue instanceof SerializableRepository) {
                        ((SerializableRepository) targetValue).merge(value);
                    }
                    continue;
                }

                mergeNormalField(declaredField, modifiers, value, targetValue);
            } catch (Exception e) {
                getLogger().error("Failed to update field {} onto {}", declaredField.getName(), this.getClass().getSimpleName(), e);
            }
        }
    }

    private void mergeNormalField(Field declaredField, int modifiers, Object value, Object targetValue) throws IllegalAccessException {
        if (Modifier.isFinal(modifiers)) {
            if (value instanceof Vector3f vector3f) {
                ((Vector3f) targetValue).set(vector3f);
            } else if (Map.class.isAssignableFrom(value.getClass())) {
                ((Map<?, ?>) targetValue).clear();
                ((Map<?, ?>) targetValue).putAll((Map) value);
            } else if (value instanceof ImVec4 imVec4) {
                ((ImVec4) targetValue).set(imVec4);
            } else if (value instanceof ImInt imInt) {
                ((ImInt) targetValue).set(imInt);
            } else if (List.class.isAssignableFrom(value.getClass())) {
                List<?> t = (List<?>) targetValue;
                t.clear();
                t.addAll((List) value);
            } else {
                declaredField.set(this, value);
            }
        } else {
            declaredField.set(this, value);
        }
    }
}
