package com.pine.engine.service.streaming.data;

import com.pine.engine.repository.streaming.StreamableResourceType;
import org.lwjgl.opengl.GL46;

import java.nio.ByteBuffer;

public class TextureStreamData implements StreamData {
    public final int width;
    public final int height;
    public final ByteBuffer imageBuffer;
    public final int internalFormat;
    public final int format;
    public final int type;
    public final boolean mipmap;

    public TextureStreamData(int width, int height, ByteBuffer imageBuffer, boolean mipmap) {
        this.width = width;
        this.height = height;
        this.imageBuffer = imageBuffer;
        this.internalFormat = GL46.GL_RGBA16;
        this.format = GL46.GL_RGBA;
        this.type = GL46.GL_UNSIGNED_BYTE;
        this.mipmap = mipmap;
    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.TEXTURE;
    }
}
