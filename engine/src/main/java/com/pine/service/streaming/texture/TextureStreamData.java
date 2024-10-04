package com.pine.service.streaming.texture;

import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.streaming.StreamLoadData;

public class TextureStreamData implements StreamLoadData {
    public TextureStreamData() {
        super();
        // TODO
    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.TEXTURE;
    }
}
