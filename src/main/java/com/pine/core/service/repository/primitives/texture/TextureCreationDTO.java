package com.pine.core.service.repository.primitives.texture;

import com.pine.core.service.ResourceType;
import com.pine.core.service.common.IResourceCreationData;

public class TextureCreationDTO implements IResourceCreationData {
    @Override
    public ResourceType getResourceType() {
        return ResourceType.TEXTURE;
    }
}
