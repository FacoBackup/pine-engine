package com.jengine.app.core.service.serialization;


import com.google.gson.JsonObject;

import java.util.Map;

public abstract class SerializableRepository implements SerializableResource {
    public static final String INSTANCE_KEY = "instanceID";
    public static final String CLASS_KEY = "className";
    public static final String DATA_KEY = "data";

    @Override
    public String getInstanceId() {
        return null;
    }


    public void parse(JsonObject data) {
        parseInternal(data.getAsJsonObject(DATA_KEY));
    }

    @Override
    public void parse(JsonObject json, Map<String, SerializableResource> instancesMap) {
        parse(json);
    }

    abstract protected void parseInternal(JsonObject data);
}
