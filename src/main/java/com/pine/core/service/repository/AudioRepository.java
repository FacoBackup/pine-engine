package com.pine.core.service.repository;

import com.pine.core.service.common.IResource;
import com.pine.core.service.common.IResourceRepository;
import com.pine.core.service.repository.primitives.audio.Audio;
import com.pine.core.service.repository.primitives.audio.AudioDTO;
import org.springframework.stereotype.Repository;

@Repository
public class AudioRepository implements IResourceRepository< AudioDTO, AudioDTO> {

    @Override
    public void bind(String id, AudioDTO data) {

    }

    @Override
    public void bind(String id) {

    }

    @Override
    public void unbind() {

    }

    @Override
    public <T extends IResource> T add(AudioDTO data) {
        return null;
    }

    @Override
    public void remove(String id) {

    }
}
