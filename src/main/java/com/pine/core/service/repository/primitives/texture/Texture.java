package com.pine.core.service.repository.primitives.texture;

import com.pine.core.service.ResourceType;
import com.pine.core.service.common.AbstractResource;

public class Texture extends AbstractResource<TextureCreationDTO> {
    public Texture(String id, TextureCreationDTO dto) {
        super(id);
    }

    @Override
    public ResourceType getResourceType() {
        return null;
    }
}
