package com.pine.engine.core.repository;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pine.engine.core.EngineDependency;
import com.pine.engine.core.EngineInjectable;
import com.pine.engine.core.service.entity.EntityRepository;
import com.pine.engine.core.service.serialization.SerializableRepository;

@EngineInjectable
public class EngineRepository extends SerializableRepository {

    @EngineDependency
    public CameraRepository cameraRepository;

    @EngineDependency
    public ResourceLoaderRepository resourceLoaderRepository;

    @EngineDependency
    public EntityRepository entityRepository;

    @Override
    public JsonElement serializeData() {
        JsonArray arr = new JsonArray();
        arr.add(entityRepository.serialize().toString());
        arr.add(cameraRepository.serialize().toString());
        arr.add(resourceLoaderRepository.serialize().toString());
        return arr;
    }

    @Override
    protected void parseInternal(JsonElement data) {
        JsonArray json = data.getAsJsonArray();
        json.forEach(a -> {
            JsonObject obj = a.getAsJsonObject();
            if (entityRepository.isCompatible(obj)) {
                entityRepository.parse(obj);
            }
            if (resourceLoaderRepository.isCompatible(obj)) {
                resourceLoaderRepository.parse(obj);
            }
            if (cameraRepository.isCompatible(obj)) {
                cameraRepository.parse(obj);
            }
        });
    }
}
