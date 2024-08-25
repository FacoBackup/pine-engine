package com.pine.core.service.repository;

import com.pine.core.service.common.IResource;
import com.pine.core.service.common.IResourceRepository;
import com.pine.core.service.repository.primitives.EmptyRuntimeData;
import com.pine.core.service.repository.primitives.texture.Texture;
import com.pine.core.service.repository.primitives.texture.TextureCreationDTO;
import org.springframework.stereotype.Repository;

@Repository
public class TextureRepository implements IResourceRepository< EmptyRuntimeData, TextureCreationDTO> {

    @Override
    public void bind(String id, EmptyRuntimeData data) {

    }

    @Override
    public void bind(String id) {

    }

    @Override
    public void unbind() {

    }

    @Override
    public <T extends IResource> T add(TextureCreationDTO data) {
        return null;
    }

    @Override
    public void remove(String id) {

    }
}
