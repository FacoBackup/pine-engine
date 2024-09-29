package com.pine.service.loader.impl.response;

import com.pine.FSUtil;
import com.pine.service.loader.impl.info.LoadRequest;
import com.pine.service.resource.resource.ResourceType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AudioLoaderResponse extends AbstractLoaderResponse {
    private List<String> audio;

    @Override
    public ResourceType getResourceType() {
        return ResourceType.AUDIO;
    }

    public AudioLoaderResponse(boolean isLoaded, LoadRequest request, List<String> audio) {
        super(isLoaded, request, audio.stream().map(a -> new ResourceInfo(FSUtil.getNameFromPath(request.path()), a)).collect(Collectors.toList()));
        this.audio = audio;
    }

    public List<String> getAudio() {
        return audio;
    }

    public void setAudio(List<String> audio) {
        this.audio = audio;
    }
}
