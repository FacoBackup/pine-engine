package com.pine.engine.service.primitives.texture;

import com.pine.common.resource.ResourceType;
import com.pine.common.resource.AbstractResource;

public class Texture extends AbstractResource<TextureCreationDTO> {
    public Texture(String id, TextureCreationDTO dto) {
        super(id);
    }

    @Override
    public ResourceType getResourceType() {
        return null;
    }
}
