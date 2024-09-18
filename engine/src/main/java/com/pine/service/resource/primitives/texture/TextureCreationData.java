package com.pine.service.resource.primitives.texture;

import com.pine.service.resource.resource.ResourceCreationData;
import com.pine.service.resource.resource.ResourceType;

public class TextureCreationData implements ResourceCreationData {
    public TextureCreationData() {
        super();
        // TODO
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.TEXTURE;
    }
}
