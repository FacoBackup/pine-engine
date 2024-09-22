package com.pine;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL46;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

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

    public static void copyWithOffset(FloatBuffer target, Matrix4f m, int offset) {
        target.put(offset, m.get(0, 0));
        target.put(1 + offset, m.get( 0,1));
        target.put(2 + offset, m.get( 0,2));
        target.put(3 + offset, m.get( 0,3));
        target.put(4 + offset, m.get( 1,0));
        target.put(5 + offset, m.get( 1,1));
        target.put(6 + offset, m.get( 1,2));
        target.put(7 + offset, m.get( 1,3));
        target.put(8 + offset, m.get( 2,0));
        target.put(9 + offset, m.get( 2,1));
        target.put(10 + offset, m.get( 2,2));
        target.put(11 + offset, m.get( 2,3));
        target.put(12 + offset, m.get( 3,0));
        target.put(13 + offset, m.get( 3,1));
        target.put(14 + offset, m.get( 3,2));
        target.put(15 + offset, m.get( 3,3));
    }

    public static void copyWithOffset(FloatBuffer target, Vector3f v, int offset) {
        target.put(offset, v.x);
        target.put(1 + offset, v.y);
        target.put(2 + offset, v.z);
    }
}
