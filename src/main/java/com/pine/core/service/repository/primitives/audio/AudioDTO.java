package com.pine.core.service.repository.primitives.audio;

import com.pine.core.service.ResourceType;
import com.pine.core.service.common.IResourceCreationData;
import com.pine.core.service.common.IResourceRuntimeData;

public class AudioDTO implements IResourceCreationData, IResourceRuntimeData {
    @Override
    public ResourceType getResourceType() {
        return ResourceType.AUDIO;
    }
}
