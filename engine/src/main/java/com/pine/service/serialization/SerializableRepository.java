package com.pine.service.serialization;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Objects;

public abstract class SerializableRepository implements SerializableInstance {
    public static final String CLASS_KEY = "className";
    public static final String DATA_KEY = "data";

    @Override
    public JsonObject serialize() {
        JsonObject json = new JsonObject();

        json.add(DATA_KEY, serializeData());
        json.addProperty(CLASS_KEY, getClass().getName());
        return json;
    }

    @Override
    public void parse(JsonObject data) {
        parseInternal(data.get(DATA_KEY));
    }

    abstract protected void parseInternal(JsonElement data);

    public boolean isCompatible(JsonObject data) {
        return Objects.equals(data.get(CLASS_KEY).getAsString(), this.getClass().getName());
    }
}
