package com.pine.repository;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pine.PBean;
import com.pine.PInject;
import com.pine.service.serialization.SerializableRepository;

@PBean
public class EngineRepository extends SerializableRepository {

    @PInject
    public CameraRepository cameraRepository;

    @PInject
    public ResourceLoaderRepository resourceLoaderRepository;

    @PInject
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
