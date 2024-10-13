package com.pine.service.streaming.texture;

import com.pine.repository.streaming.AbstractStreamableResource;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.theme.Icons;
import org.lwjgl.opengl.GL46;
import org.lwjgl.stb.STBImage;

public class TextureStreamableResource extends AbstractStreamableResource<TextureStreamData> {
    public int texture;

    public TextureStreamableResource(String pathToFile, String id) {
        super(pathToFile, id);
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
        STBImage.stbi_image_free(data.imageBuffer);
    }

    @Override
    protected void disposeInternal() {
        GL46.glDeleteTextures(texture);
    }

    @Override
    public String getIcon() {
        return Icons.texture;
    }
}
