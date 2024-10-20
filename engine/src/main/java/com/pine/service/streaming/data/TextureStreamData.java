package com.pine.service.streaming.data;

import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.streaming.StreamData;

import java.nio.ByteBuffer;

public class TextureStreamData implements StreamData {
    public final int width;
    public final int height;
    public final ByteBuffer imageBuffer;

    public TextureStreamData(int width, int height, ByteBuffer imageBuffer) {
        this.width = width;
        this.height = height;
        this.imageBuffer = imageBuffer;
    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.TEXTURE;
    }
}
