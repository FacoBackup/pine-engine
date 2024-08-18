package com.jengine.app.core.components.serialization;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Field;

import static com.jengine.app.core.components.serialization.SerializableRepository.*;

public interface SerializableResource extends SerializableInstance {

    String getInstanceId();

    void setInstanceId(String id);

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

    default void parse(JsonObject json) {
        setInstanceId(json.get(INSTANCE_KEY).getAsString());
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
