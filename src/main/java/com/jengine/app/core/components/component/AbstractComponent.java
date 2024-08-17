package com.jengine.app.core.components.component;

import com.artemis.Component;
import com.jengine.app.core.service.serialization.SerializableResource;

import java.util.List;
import java.util.UUID;

public abstract class AbstractComponent extends Component implements SerializableResource {
    private final String instanceId;

    public AbstractComponent() {
        instanceId = UUID.randomUUID().toString();
    }

    public AbstractComponent(String id) {
        instanceId = id;
    }

    @Override
    public String getInstanceId() {
        return instanceId;
    }

    public abstract List<Class<? extends AbstractComponent>> getDependencies();
}
