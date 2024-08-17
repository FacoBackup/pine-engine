package com.jengine.app.core.service.serialization;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jengine.app.Loggable;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;

import static com.jengine.app.core.service.serialization.SerializableRepository.*;

public interface SerializableResource extends Loggable {

    String getInstanceId();

    default JsonElement serializeData() {
        return (new Gson()).toJsonTree(this);
    }

    default JsonObject serialize() {
        JsonObject json = new JsonObject();

        json.addProperty(INSTANCE_KEY, getInstanceId());
        json.add(DATA_KEY, serializeData());
        json.addProperty(CLASS_KEY, getClass().getName());
        return json;
    }

    default void parse(JsonObject json, Map<String, SerializableResource> instancesMap) {
        final Gson gson = new Gson();
        try {
            Field[] fields = this.getClass().getDeclaredFields();
            for (Field field : fields) {
                Class<?> type = field.getType();
                if (SerializableResource.class.isAssignableFrom(type)) {
                    field.setAccessible(true);
                    String fieldName = field.getName();

                    if (type.isAssignableFrom(SerializableResource.class)) {
                        final String instanceId = gson.fromJson(fieldName, String.class);
                        field.set(this, instancesMap.get(instanceId));
                    } else {
                        Object fieldValue = gson.fromJson(fieldName, type);
                        field.set(this, fieldValue);
                    }
                }
            }
        } catch (Exception e) {
            getLogger().error("Failed to parse serialized resource", e);
        }
    }

    default boolean isCompatible(JsonObject data) {
        return Objects.equals(data.get(CLASS_KEY).getAsString(), this.getClass().getName());
    }
}
