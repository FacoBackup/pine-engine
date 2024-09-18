package com.pine.core.repository;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pine.core.EngineDependency;
import com.pine.core.EngineInjectable;
import com.pine.core.service.serialization.SerializableRepository;

@EngineInjectable
public class EngineRepository extends SerializableRepository {

    @EngineDependency
    public CameraRepository cameraRepository;

    @EngineDependency
    public ResourceLoaderRepository resourceLoaderRepository;

    @EngineDependency
    public WorldRepository worldRepository;

    @Override
    public JsonElement serializeData() {
        JsonArray arr = new JsonArray();
        arr.add(worldRepository.serialize().toString());
        arr.add(cameraRepository.serialize().toString());
        arr.add(resourceLoaderRepository.serialize().toString());
        return arr;
    }

    @Override
    protected void parseInternal(JsonElement data) {
        JsonArray json = data.getAsJsonArray();
        json.forEach(a -> {
            JsonObject obj = a.getAsJsonObject();
            if (worldRepository.isCompatible(obj)) {
                worldRepository.parse(obj);
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
