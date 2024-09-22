package com.pine.repository;

import com.google.gson.JsonElement;
import com.pine.PBean;
import com.pine.service.loader.impl.response.AbstractLoaderResponse;
import com.pine.service.serialization.SerializableRepository;

import java.util.ArrayList;
import java.util.List;

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
