package com.pine.engine.core.service.primitives.audio;

import com.pine.engine.core.resource.IResourceCreationData;
import com.pine.engine.core.resource.IResourceRuntimeData;
import com.pine.engine.core.resource.ResourceType;

public class AudioDTO implements IResourceCreationData, IResourceRuntimeData {
    @Override
    public ResourceType getResourceType() {
        return ResourceType.AUDIO;
    }
}
