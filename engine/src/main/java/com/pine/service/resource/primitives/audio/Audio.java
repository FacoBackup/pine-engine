package com.pine.service.resource.primitives.audio;

import com.pine.service.resource.resource.AbstractResource;
import com.pine.service.resource.resource.ResourceType;

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
