package com.pine.engine.service;

import com.pine.common.resource.IResource;
import com.pine.common.resource.ResourceService;
import com.pine.engine.service.primitives.audio.Audio;
import com.pine.engine.service.primitives.audio.AudioDTO;
import org.springframework.stereotype.Repository;

@Repository
public class AudioService implements ResourceService<Audio, AudioDTO, AudioDTO> {

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
