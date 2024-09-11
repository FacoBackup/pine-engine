package com.pine.engine.core.service.entity;

import com.google.gson.JsonElement;
import com.pine.engine.core.service.EngineInjectable;
import com.pine.engine.core.service.serialization.SerializableRepository;

public class EntityService extends SerializableRepository implements EngineInjectable {
    private final EntityRepository entityRepository = new EntityRepository();

    @Override
    protected void parseInternal(JsonElement data) {
        entityRepository.parseInternal(data);
    }

    @Override
    public JsonElement serializeData() {
        return entityRepository.serializeData();
    }
}
