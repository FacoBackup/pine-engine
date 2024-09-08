package com.pine.engine.core.service.resource.primitives.texture;

import com.pine.engine.core.service.resource.resource.ResourceCreationData;
import com.pine.engine.core.service.resource.resource.ResourceType;

public class TextureCreationData extends ResourceCreationData {
    public TextureCreationData() {
        super();
        // TODO
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.TEXTURE;
    }
}
