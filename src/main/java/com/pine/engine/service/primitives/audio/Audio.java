package com.pine.engine.service.primitives.audio;

import com.pine.engine.resource.AbstractResource;
import com.pine.engine.resource.ResourceType;

public class Audio extends AbstractResource<AudioDTO> {
    public Audio(String id) {
        super(id);
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.AUDIO;
    }
}
