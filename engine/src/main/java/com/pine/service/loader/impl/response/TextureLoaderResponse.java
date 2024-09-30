package com.pine.service.loader.impl.response;

import com.pine.FSUtil;
import com.pine.service.loader.impl.info.LoadRequest;
import com.pine.service.resource.resource.ResourceType;

import java.util.List;
import java.util.stream.Collectors;

public class TextureLoaderResponse extends AbstractLoaderResponse {
    private List<String> textures;

    @Override
    public ResourceType getResourceType() {
        return ResourceType.TEXTURE;
    }

    public TextureLoaderResponse(boolean isLoaded, LoadRequest request, List<String> textures) {
        super(isLoaded, request, textures.stream().map(a -> new ResourceInfo(FSUtil.getNameFromPath(request.path()), a)).collect(Collectors.toList()));
        this.textures = textures;
    }

    public List<String> getTextures() {
        return textures;
    }

    public void setTextures(List<String> textures) {
        this.textures = textures;
    }
}
