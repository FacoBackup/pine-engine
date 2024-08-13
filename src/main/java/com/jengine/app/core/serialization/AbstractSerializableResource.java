package com.jengine.app.core.serialization;

import com.google.gson.Gson;

import java.util.Map;
import java.util.UUID;

public abstract class AbstractSerializableResource {
    private final String instanceId;

    public AbstractSerializableResource(String instanceId) {
        this.instanceId = instanceId;
    }

    public AbstractSerializableResource() {
        instanceId = UUID.randomUUID().toString();
    }

    public String getInstanceId() {
        return instanceId;
    }

    public abstract Gson toJson();

    public abstract void fromJson(Gson gson, Map<String, AbstractSerializableResource> instancesMap);
}
