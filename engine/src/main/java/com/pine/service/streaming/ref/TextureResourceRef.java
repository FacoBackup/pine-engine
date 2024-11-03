package com.pine.service.streaming.ref;

import com.pine.repository.streaming.AbstractResourceRef;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.streaming.data.TextureStreamData;
import org.lwjgl.opengl.GL46;
import org.lwjgl.opengl.GL46C;
import org.lwjgl.stb.STBImage;

public class TextureResourceRef extends AbstractResourceRef<TextureStreamData> {
    public int texture;
    public int width;
    public int height;
    public boolean isFramebuffer;
    public FrameBufferObject frameBuffer;

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

        GL46.glTexImage2D(GL46.GL_TEXTURE_2D, 0, GL46.GL_RGBA, data.width, data.height, 0, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, data.imageBuffer);

        GL46.glGenerateMipmap(GL46.GL_TEXTURE_2D);
        GL46C.glBindTexture(GL46C.GL_TEXTURE_2D, GL46.GL_NONE);
        STBImage.stbi_image_free(data.imageBuffer);

        this.width = data.width;
        this.height = data.height;
    }

    @Override
    protected void disposeInternal() {
        if (frameBuffer != null) {
            frameBuffer.dispose();
        }else{
            GL46.glDeleteTextures(texture);
        }
    }

    public void genFramebuffer() {
        if(!isLoaded()){
            return;
        }
        frameBuffer = new FrameBufferObject(width, height);
        frameBuffer.registerTexture(0, GL46.GL_RGBA8, texture);
    }
}
