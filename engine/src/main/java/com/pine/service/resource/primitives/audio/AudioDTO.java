package com.pine.service.resource.primitives.audio;

import com.pine.service.resource.resource.IResourceRuntimeData;
import com.pine.service.resource.resource.ResourceCreationData;
import com.pine.service.resource.resource.ResourceType;

public class AudioDTO extends ResourceCreationData implements IResourceRuntimeData {
    @Override
    public ResourceType getResourceType() {
        return ResourceType.AUDIO;
    }
}
