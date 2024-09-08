package com.pine.engine.core.service.resource.primitives.audio;

import com.pine.engine.core.service.resource.resource.ResourceCreationData;
import com.pine.engine.core.service.resource.resource.IResourceRuntimeData;
import com.pine.engine.core.service.resource.resource.ResourceType;

public class AudioDTO extends ResourceCreationData implements IResourceRuntimeData {
    public AudioDTO() {
        super();
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.AUDIO;
    }
}
