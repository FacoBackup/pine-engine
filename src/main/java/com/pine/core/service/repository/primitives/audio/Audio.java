package com.pine.core.service.repository.primitives.audio;

import com.pine.core.service.ResourceType;
import com.pine.core.service.common.AbstractResource;

public class Audio extends AbstractResource<AudioDTO> {
    public Audio(String id) {
        super(id);
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.AUDIO;
    }
}
