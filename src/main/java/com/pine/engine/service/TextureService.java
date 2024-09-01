package com.pine.engine.service;

import com.pine.common.resource.IResource;
import com.pine.common.resource.ResourceService;
import com.pine.engine.service.primitives.EmptyRuntimeData;
import com.pine.engine.service.primitives.texture.Texture;
import com.pine.engine.service.primitives.texture.TextureCreationDTO;
import org.springframework.stereotype.Repository;

@Repository
public class TextureService implements ResourceService<Texture, EmptyRuntimeData, TextureCreationDTO> {

    @Override
    public void bind(Texture instance, EmptyRuntimeData data) {

    }

    @Override
    public void bind(Texture instance) {

    }

    @Override
    public void unbind() {

    }

    @Override
    public IResource add(TextureCreationDTO data) {
        return null;
    }

    @Override
    public void remove(Texture id) {

    }
}
