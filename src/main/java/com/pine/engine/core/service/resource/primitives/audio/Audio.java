package com.pine.engine.core.service.resource.primitives.audio;

import com.pine.engine.core.service.resource.resource.AbstractResource;
import com.pine.engine.core.service.resource.resource.ResourceType;

public class Audio extends AbstractResource {
    public Audio(String id) {
        super(id);
        // TODO
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.AUDIO;
    }
}
