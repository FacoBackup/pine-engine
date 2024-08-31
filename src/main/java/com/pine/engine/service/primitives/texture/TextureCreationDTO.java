package com.pine.engine.service.primitives.texture;

import com.pine.common.resource.ResourceType;
import com.pine.common.resource.IResourceCreationData;

public class TextureCreationDTO implements IResourceCreationData {
    @Override
    public ResourceType getResourceType() {
        return ResourceType.TEXTURE;
    }
}
