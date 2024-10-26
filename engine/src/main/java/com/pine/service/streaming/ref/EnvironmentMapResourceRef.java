package com.pine.service.streaming.ref;

import com.pine.repository.streaming.AbstractResourceRef;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.streaming.data.EnvironmentMapStreamData;
import com.pine.service.streaming.impl.CubeMapFace;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL46;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;

import static com.pine.service.environment.CubeMapGenerator.setUpCubeMapTexture;

public class EnvironmentMapResourceRef extends AbstractResourceRef<EnvironmentMapStreamData> {
    public int texture;

    public EnvironmentMapResourceRef(String id) {
        super(id);
    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.ENVIRONMENT_MAP;
    }

    @Override
    protected void loadInternal(EnvironmentMapStreamData data) {
        this.texture = GL46.glGenTextures();
        GL46.glBindTexture(GL46.GL_TEXTURE_CUBE_MAP, texture);

        ByteBuffer[] images = data.images();
        for (int i = 0; i < CubeMapFace.values().length; i++) {
            GL46.glTexImage2D(
                    CubeMapFace.values()[i].getGlFace(), 0,
                    GL46.GL_RGBA8, data.imageSize(), data.imageSize(), 0,
                    GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, images[i]
            );
            STBImage.stbi_image_free(images[i]);
        }

        setUpCubeMapTexture();
        GL46.glBindTexture(GL46.GL_TEXTURE_CUBE_MAP, GL11.GL_NONE);
    }

    @Override
    protected void disposeInternal() {
        GL46.glDeleteTextures(texture);
    }
}
