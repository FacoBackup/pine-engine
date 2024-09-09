package com.pine.engine.core.service.entity;

import com.google.gson.JsonElement;
import com.pine.engine.core.service.serialization.SerializableRepository;

public class EntityRepository extends SerializableRepository {
    @Override
    protected void parseInternal(JsonElement data) {
        // TODO
    }

    @Override
    public JsonElement serializeData() {
        // TODO
        return null;
    }
}
