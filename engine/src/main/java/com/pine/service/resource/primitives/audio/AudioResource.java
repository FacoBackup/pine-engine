package com.pine.service.resource.primitives.audio;

import com.pine.service.resource.resource.AbstractResource;
import com.pine.service.resource.resource.ResourceType;

public class AudioResource extends AbstractResource {
    public AudioResource(String id) {
        super(id);
        // TODO
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.AUDIO;
    }

    @Override
    public void dispose() {
    }
}
