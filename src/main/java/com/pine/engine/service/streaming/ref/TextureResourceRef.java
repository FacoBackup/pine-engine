package com.pine.engine.service.streaming.ref;

import com.pine.engine.repository.streaming.AbstractResourceRef;
import com.pine.engine.repository.streaming.StreamableResourceType;
import com.pine.engine.service.resource.fbo.FBO;
import com.pine.engine.service.streaming.data.TextureStreamData;
import org.lwjgl.opengl.GL46;
import org.lwjgl.opengl.GL46C;
import org.lwjgl.stb.STBImage;

public class TextureResourceRef extends AbstractResourceRef<TextureStreamData> {
    public int texture;
    public int width;
    public int height;
    public int depth;
    public int internalFormat;
    public int format;
    public int type;
    public FBO frameBuffer;

    public TextureResourceRef(String id) {
        super(id);
    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.TEXTURE;
    }

    @Override
    protected void loadInternal(TextureStreamData data) {
        texture = GL46.glGenTextures();
        GL46.glBindTexture(GL46.GL_TEXTURE_2D, texture);

        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_WRAP_S, GL46.GL_REPEAT);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_WRAP_T, GL46.GL_REPEAT);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MIN_FILTER, GL46.GL_LINEAR);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MAG_FILTER, GL46.GL_LINEAR);

        GL46.glTexImage2D(GL46.GL_TEXTURE_2D, 0, data.internalFormat, data.width, data.height, 0, data.format, data.type, data.imageBuffer);

        if (data.mipmap) {
            GL46.glGenerateMipmap(GL46.GL_TEXTURE_2D);
        }
        GL46C.glBindTexture(GL46C.GL_TEXTURE_2D, GL46.GL_NONE);

        STBImage.stbi_image_free(data.imageBuffer);

        internalFormat = data.internalFormat;
        format = data.format;
        type = data.type;
        width = data.width;
        height = data.height;
    }

    @Override
    protected void disposeInternal() {
        if (frameBuffer != null) {
            frameBuffer.dispose();
        } else {
            GL46.glDeleteTextures(texture);
        }
    }

    public void bindForWriting(int unit) {
        GL46.glBindImageTexture(unit, texture, 0, false, 0, GL46.GL_WRITE_ONLY, internalFormat);
    }

    public void bindForWriting3d(int unit) {
        GL46.glBindImageTexture(unit, texture, 0, true, 0, GL46.GL_WRITE_ONLY, internalFormat);
    }

    public void bindForBoth(int unit) {
        GL46.glBindImageTexture(unit, texture, 0, false, 0, GL46.GL_READ_WRITE, internalFormat);
    }
}
