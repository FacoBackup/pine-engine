package com.pine.engine.core.service.resource.primitives.audio;

import com.pine.engine.core.service.resource.resource.IResourceRuntimeData;
import com.pine.engine.core.service.resource.resource.ResourceCreationData;
import com.pine.engine.core.service.resource.resource.ResourceType;

public class AudioDTO implements IResourceRuntimeData, ResourceCreationData {
    @Override
    public ResourceType getResourceType() {
        return ResourceType.AUDIO;
    }
}
