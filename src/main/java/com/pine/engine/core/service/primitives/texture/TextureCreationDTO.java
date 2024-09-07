package com.pine.engine.core.service.primitives.texture;

import com.pine.engine.core.resource.IResourceCreationData;
import com.pine.engine.core.resource.ResourceType;

public class TextureCreationDTO implements IResourceCreationData {
    @Override
    public ResourceType getResourceType() {
        return ResourceType.TEXTURE;
    }
}
