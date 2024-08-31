package com.pine.core.service.repository;

import com.pine.core.service.common.IResource;
import com.pine.core.service.common.IResourceService;
import com.pine.core.service.repository.primitives.EmptyRuntimeData;
import com.pine.core.service.repository.primitives.texture.Texture;
import com.pine.core.service.repository.primitives.texture.TextureCreationDTO;
import org.springframework.stereotype.Repository;

@Repository
public class TextureService implements IResourceService<Texture, EmptyRuntimeData, TextureCreationDTO> {

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
