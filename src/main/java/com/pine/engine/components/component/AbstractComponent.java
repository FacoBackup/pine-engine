package com.pine.engine.components.component;

import com.artemis.Component;
import com.pine.common.serialization.SerializableResource;

import java.util.List;
import java.util.UUID;

public abstract class AbstractComponent extends Component implements SerializableResource {
    private String instanceId;

    public AbstractComponent() {
        instanceId = UUID.randomUUID().toString();
    }

    @Override
    public String getInstanceId() {
        return instanceId;
    }

    @Override
    public void setInstanceId(String id) {
        instanceId = id;
    }

    public abstract List<Class<? extends AbstractComponent>> getDependencies();
}
