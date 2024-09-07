package com.pine.engine.service;

import com.pine.engine.resource.AbstractResourceService;
import com.pine.engine.resource.IResource;
import com.pine.engine.resource.ResourceType;
import com.pine.engine.service.primitives.audio.Audio;
import com.pine.engine.service.primitives.audio.AudioDTO;

public class AudioService extends AbstractResourceService<Audio, AudioDTO, AudioDTO> {

    @Override
    public void bindInternal(Audio instance, AudioDTO data) {

    }

    @Override
    public void bindInternal(Audio instance) {

    }

    @Override
    public void unbind() {

    }

    @Override
    public IResource addInternal(AudioDTO data) {
        return null;
    }

    @Override
    public void removeInternal(Audio id) {

    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.AUDIO;
    }
}
