package com.pine.engine.service.primitives.audio;

import com.pine.common.resource.ResourceType;
import com.pine.common.resource.IResourceCreationData;
import com.pine.common.resource.IResourceRuntimeData;

public class AudioDTO implements IResourceCreationData, IResourceRuntimeData {
    @Override
    public ResourceType getResourceType() {
        return ResourceType.AUDIO;
    }
}
