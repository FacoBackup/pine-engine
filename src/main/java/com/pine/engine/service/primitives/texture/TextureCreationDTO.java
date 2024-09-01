package com.pine.engine.service.primitives.texture;

import com.pine.common.resource.IResourceCreationData;
import com.pine.common.resource.ResourceType;

public class TextureCreationDTO implements IResourceCreationData {
    @Override
    public ResourceType getResourceType() {
        return ResourceType.TEXTURE;
    }
}
