package com.pine.service.resource;

import com.pine.PBean;
import com.pine.service.resource.primitives.audio.Audio;
import com.pine.service.resource.primitives.audio.AudioDTO;
import com.pine.service.resource.resource.AbstractResourceService;
import com.pine.service.resource.resource.IResource;
import com.pine.service.resource.resource.ResourceType;

@PBean
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
