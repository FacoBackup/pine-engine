package com.pine.engine.core.repository;

import jakarta.annotation.Nullable;
import org.lwjgl.opengl.GL46;

import java.nio.ByteBuffer;

public class EngineUtils {
    public static void bindTexture2d(int location, int activeIndex, int sampler) {
        GL46.glActiveTexture(GL46.GL_TEXTURE0 + activeIndex);
        GL46.glBindTexture(GL46.GL_TEXTURE_2D, sampler);
        GL46.glUniform1i(location, activeIndex);
    }

    public static int createTexture(
            int width,
            int height,
            int internalFormat,
            int border,
            int format,
            int type,
            @Nullable ByteBuffer data,
            int minFilter,
            int magFilter,
            int wrapS,
            int wrapT,
            boolean autoUnbind
    ) {
        int texture = GL46.glGenTextures();
        GL46.glBindTexture(GL46.GL_TEXTURE_2D, texture);
        GL46.glTexImage2D(
                GL46.GL_TEXTURE_2D,
                0,
                internalFormat,
                width,
                height,
                border,
                format,
                type,
                data
        );
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MAG_FILTER, magFilter);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MIN_FILTER, minFilter);
        if (wrapS != 0) {
            GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_WRAP_S, wrapS);
        }
        if (wrapT != 0) {
            GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_WRAP_T, wrapT);
        }

        if (autoUnbind) {
            GL46.glBindTexture(GL46.GL_TEXTURE_2D, 0);
        }
        return texture;
    }

}
