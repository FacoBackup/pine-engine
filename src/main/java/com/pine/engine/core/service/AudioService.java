package com.pine.engine.core.service;

import com.pine.engine.core.resource.AbstractResourceService;
import com.pine.engine.core.resource.IResource;
import com.pine.engine.core.resource.ResourceType;
import com.pine.engine.core.service.primitives.audio.Audio;
import com.pine.engine.core.service.primitives.audio.AudioDTO;

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
