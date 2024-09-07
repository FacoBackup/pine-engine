package com.pine.engine.service.primitives.texture;

import com.pine.engine.resource.IResourceCreationData;
import com.pine.engine.resource.ResourceType;

public class TextureCreationDTO implements IResourceCreationData {
    @Override
    public ResourceType getResourceType() {
        return ResourceType.TEXTURE;
    }
}
