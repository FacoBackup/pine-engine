package com.pine.core.service.loader.impl.response;

import java.util.List;

public class AudioLoaderResponse extends AbstractLoaderResponse {
    private List<String> audio;

    public AudioLoaderResponse(){}

    public AudioLoaderResponse(boolean isLoaded, String filePath, List<String> audio) {
        super(isLoaded, filePath);
        this.audio = audio;
    }

    public List<String> getAudio() {
        return audio;
    }

    public void setAudio(List<String> audio) {
        this.audio = audio;
    }
}
