package com.pine.engine.core.service.loader.impl.response;

import java.util.List;

public class TextureLoaderResponse extends AbstractLoaderResponse {
    private List<String> textures;

    public TextureLoaderResponse() {
    }

    public TextureLoaderResponse(boolean isLoaded, String filePath, List<String> textures) {
        super(isLoaded, filePath);
        this.textures = textures;
    }

    public List<String> getTextures() {
        return textures;
    }

    public void setTextures(List<String> textures) {
        this.textures = textures;
    }
}
