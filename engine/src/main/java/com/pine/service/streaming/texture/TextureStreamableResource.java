package com.pine.service.streaming.texture;

import com.pine.service.resource.AbstractResource;
import com.pine.service.resource.LocalResourceType;

public class TextureStreamableResource extends AbstractResource {
    public TextureStreamableResource(String id, TextureStreamData dto) {
        super(id);

        // TODO
    }

    @Override
    public LocalResourceType getResourceType() {
        return LocalResourceType.TEXTURE;
    }

    @Override
    public void dispose() {

    }
}
