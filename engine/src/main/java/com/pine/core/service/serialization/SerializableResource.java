package com.pine.core.service.serialization;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Field;

import static com.pine.core.service.serialization.SerializableRepository.CLASS_KEY;
import static com.pine.core.service.serialization.SerializableRepository.DATA_KEY;

public interface SerializableResource extends SerializableInstance {

    default JsonElement serializeData() {
        return (new Gson()).toJsonTree(this);
    }

    default JsonObject serialize() {
        JsonObject json = new JsonObject();

        json.add(DATA_KEY, serializeData());
        json.addProperty(CLASS_KEY, getClass().getName());
        return json;
    }

    default void parse(JsonObject json) {
        var data = json.get(DATA_KEY).getAsJsonObject();
        try {
            Field[] fields = this.getClass().getDeclaredFields();
            for (Field field : fields) {
                Class<?> type = field.getType();
                if (SerializableInstance.class.isAssignableFrom(type)) {
                    field.setAccessible(true);
                    String fieldName = field.getName();
                    Gson gson = new Gson();
                    Object value = gson.fromJson(data.get(fieldName), type);
                    field.set(this, value);
                }
            }
        } catch (Exception e) {
            getLogger().error("Failed to parse serialized resource", e);
        }
    }
}
