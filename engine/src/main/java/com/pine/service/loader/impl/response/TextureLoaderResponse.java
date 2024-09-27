package com.pine.service.loader.impl.response;

import com.pine.FSUtil;
import com.pine.service.resource.resource.ResourceType;

import java.util.List;
import java.util.stream.Collectors;

public class TextureLoaderResponse extends AbstractLoaderResponse {
    private List<String> textures;

    @Override
    public ResourceType getResourceType() {
        return ResourceType.TEXTURE;
    }

    public TextureLoaderResponse() {
    }

    public TextureLoaderResponse(boolean isLoaded, String filePath, List<String> textures) {
        super(isLoaded, filePath, textures.stream().map(a -> new ResourceInfo(FSUtil.getNameFromPath(filePath), a)).collect(Collectors.toList()));
        this.textures = textures;
    }

    public List<String> getTextures() {
        return textures;
    }

    public void setTextures(List<String> textures) {
        this.textures = textures;
    }
}
