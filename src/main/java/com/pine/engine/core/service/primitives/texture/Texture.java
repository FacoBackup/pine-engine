package com.pine.engine.core.service.primitives.texture;

import com.pine.engine.core.resource.AbstractResource;
import com.pine.engine.core.resource.ResourceType;

public class Texture extends AbstractResource<TextureCreationDTO> {
    public Texture(String id, TextureCreationDTO dto) {
        super(id);
    }

    @Override
    public ResourceType getResourceType() {
        return null;
    }
}
