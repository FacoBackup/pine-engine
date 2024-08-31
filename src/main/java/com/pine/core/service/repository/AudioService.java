package com.pine.core.service.repository;

import com.pine.core.service.common.IResource;
import com.pine.core.service.common.IResourceService;
import com.pine.core.service.repository.primitives.audio.Audio;
import com.pine.core.service.repository.primitives.audio.AudioDTO;
import org.springframework.stereotype.Repository;

@Repository
public class AudioService implements IResourceService<Audio, AudioDTO, AudioDTO> {

    @Override
    public void bind(Audio instance, AudioDTO data) {

    }

    @Override
    public void bind(Audio instance) {

    }

    @Override
    public void unbind() {

    }

    @Override
    public IResource add(AudioDTO data) {
        return null;
    }

    @Override
    public void remove(Audio id) {

    }
}
