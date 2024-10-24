package com.pine.service.streaming.ref;

import com.pine.repository.streaming.AbstractResourceRef;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.streaming.data.EnvironmentMapStreamData;
import org.lwjgl.opengl.GL46;
import org.lwjgl.opengl.GL46C;
import org.lwjgl.stb.STBImage;

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
        int texID = GL46C.glGenTextures();
        GL46C.glBindTexture(GL46C.GL_TEXTURE_CUBE_MAP, texID);

        for (int j = 0; j < data.images().length; j++) {
            GL46C.glTexImage2D(
                    GL46C.GL_TEXTURE_CUBE_MAP_POSITIVE_X + j, 0,
                    GL46C.GL_RGBA, data.imageSize(), data.imageSize(), 0,
                    GL46C.GL_RGBA, GL46C.GL_UNSIGNED_BYTE, data.images()[j]
            );
            STBImage.stbi_image_free(data.images()[j]);
        }

        GL46C.glTexParameteri(GL46C.GL_TEXTURE_CUBE_MAP, GL46C.GL_TEXTURE_MIN_FILTER, GL46C.GL_LINEAR);
        GL46C.glTexParameteri(GL46C.GL_TEXTURE_CUBE_MAP, GL46C.GL_TEXTURE_MAG_FILTER, GL46C.GL_LINEAR);
        GL46C.glTexParameteri(GL46C.GL_TEXTURE_CUBE_MAP, GL46C.GL_TEXTURE_WRAP_S, GL46C.GL_CLAMP_TO_EDGE);
        GL46C.glTexParameteri(GL46C.GL_TEXTURE_CUBE_MAP, GL46C.GL_TEXTURE_WRAP_T, GL46C.GL_CLAMP_TO_EDGE);
        GL46C.glTexParameteri(GL46C.GL_TEXTURE_CUBE_MAP, GL46C.GL_TEXTURE_WRAP_R, GL46C.GL_CLAMP_TO_EDGE);

        GL46C.glBindTexture(GL46C.GL_TEXTURE_CUBE_MAP, 0);

        this.texture = texID;
    }

    @Override
    protected void disposeInternal() {
        GL46.glDeleteTextures(texture);
    }
}
