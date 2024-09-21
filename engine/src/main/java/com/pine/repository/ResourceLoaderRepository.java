package com.pine.repository;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pine.PBean;
import com.pine.PInject;
import com.pine.service.loader.AbstractResourceLoader;
import com.pine.service.loader.impl.response.AbstractLoaderResponse;
import com.pine.service.loader.impl.response.AudioLoaderResponse;
import com.pine.service.loader.impl.response.MeshLoaderResponse;
import com.pine.service.loader.impl.response.TextureLoaderResponse;
import com.pine.service.serialization.SerializableRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@PBean
public class ResourceLoaderRepository extends SerializableRepository {
    public final List<AbstractLoaderResponse> loadedResources = new ArrayList<>();


    @Override
    protected void parseInternal(JsonElement data) {
    }

    @Override
    public JsonElement serializeData() {
        return null;
    }
}
