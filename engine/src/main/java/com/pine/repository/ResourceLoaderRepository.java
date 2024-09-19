package com.pine.repository;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pine.annotation.EngineDependency;
import com.pine.annotation.EngineInjectable;
import com.pine.service.loader.AbstractResourceLoader;
import com.pine.service.loader.impl.response.AbstractLoaderResponse;
import com.pine.service.loader.impl.response.AudioLoaderResponse;
import com.pine.service.loader.impl.response.MeshLoaderResponse;
import com.pine.service.loader.impl.response.TextureLoaderResponse;
import com.pine.service.serialization.SerializableRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EngineInjectable
public class ResourceLoaderRepository extends SerializableRepository {
    public final List<AbstractLoaderResponse> loadedResources = new ArrayList<>();

    @EngineDependency
    public List<AbstractResourceLoader> resourceLoaders;

    @Override
    protected void parseInternal(JsonElement data) {
        data.getAsJsonArray().forEach(a -> {
            JsonObject obj = a.getAsJsonObject();
            AbstractLoaderResponse instance = null;
            if (Objects.equals(obj.get(CLASS_KEY).getAsString(), TextureLoaderResponse.class.getName())) {
                instance = new TextureLoaderResponse();
            } else if (Objects.equals(obj.get(CLASS_KEY).getAsString(), MeshLoaderResponse.class.getName())) {
                instance = new MeshLoaderResponse();
            } else if (Objects.equals(obj.get(CLASS_KEY).getAsString(), AudioLoaderResponse.class.getName())) {
                instance = new AudioLoaderResponse();
            }

            if (instance != null) {
                instance.parse(obj);
                loadedResources.add(instance);
            }
        });
    }

    @Override
    public JsonElement serializeData() {
        JsonArray jsonElements = new JsonArray();
        loadedResources.forEach(a -> {
            jsonElements.add(a.serialize());
        });
        return jsonElements;
    }

}
