package com.pine.engine.core.service.primitives.audio;

import com.pine.engine.core.resource.AbstractResource;
import com.pine.engine.core.resource.ResourceType;

public class Audio extends AbstractResource<AudioDTO> {
    public Audio(String id) {
        super(id);
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.AUDIO;
    }
}
