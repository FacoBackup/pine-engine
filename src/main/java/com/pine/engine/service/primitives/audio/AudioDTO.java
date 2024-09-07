package com.pine.engine.service.primitives.audio;

import com.pine.engine.resource.IResourceCreationData;
import com.pine.engine.resource.IResourceRuntimeData;
import com.pine.engine.resource.ResourceType;

public class AudioDTO implements IResourceCreationData, IResourceRuntimeData {
    @Override
    public ResourceType getResourceType() {
        return ResourceType.AUDIO;
    }
}
